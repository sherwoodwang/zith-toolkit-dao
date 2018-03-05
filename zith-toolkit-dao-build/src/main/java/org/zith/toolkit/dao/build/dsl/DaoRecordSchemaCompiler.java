package org.zith.toolkit.dao.build.dsl;

import com.google.common.collect.ImmutableList;
import org.zith.toolkit.dao.build.data.*;
import org.zith.toolkit.dao.build.dsl.element.*;
import org.zith.toolkit.dao.build.dsl.parser.ParserException;
import org.zith.toolkit.dao.build.dsl.parser.RecordSchemaParser;
import org.zith.toolkit.dao.build.generator.DaoSourceGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.JDBCType;
import java.util.*;
import java.util.stream.Collectors;

public class DaoRecordSchemaCompiler {

    private final DaoSourceGenerator daoSourceGenerator;

    public DaoRecordSchemaCompiler() {
        daoSourceGenerator = new DaoSourceGenerator();
    }

    public void compile(Collection<File> sources, File destroot) throws IOException, ParserException {
        Compilation compilation = new Compilation(daoSourceGenerator, destroot);

        for (File source : sources) {
            compile(compilation.unit(source), new FileReader(source), true);
        }
    }

    private synchronized void compile(Compilation.Unit unit, Reader input, boolean emitting)
            throws IOException, ParserException {
        RecordSchemaParser parser = new RecordSchemaParser(input);

        ScopeContext topLevelContext = new ScopeContext(unit, Collections.emptyList());

        RecordSchemaElement recordSchemaElement = parser.recordSchema();
        for (ImportedPathElement path : recordSchemaElement.getHead().getPaths()) {
            File importedFile = new File(unit.getFile().getParent(), path.getPath());

            compile(unit.getCompilation().unit(importedFile), new FileReader(importedFile), false);
        }

        process(topLevelContext, recordSchemaElement.getElements());

        if (emitting) {
            unit.getCompilation().emit(unit);
        }
    }

    private void process(ScopeContext context, List<BlockItemElement> elements) throws IOException {
        for (BlockItemElement element : elements) {
            switch (element.getType()) {
                case PACKAGE_SCOPE:
                    process(context, element.getPackageScopeElement());
                    break;
                case SQL_TUPLE:
                    process(context, element.getSqlTupleElement());
                    break;
                case DECLARATION:
                    process(context, element.getDeclarationElement());
                    break;
                case UTILIZATION:
                    process(context, element.getUtilizationElement());
                    break;
                case SQL_TYPE_DICTIONARY:
                    process(context, element.getSqlTypeDictionaryElement());
            }
        }
    }

    private void process(ScopeContext parentContext, PackageScopeElement scopeElement) throws IOException {
        ScopeContext context = new ScopeContext(
                parentContext,
                ImmutableList.<String>builder()
                        .addAll(parentContext.getCurrentPackage())
                        .addAll(scopeElement.getPackageNameComponents())
                        .build()
        );

        process(context, scopeElement.getElements());
    }

    private void process(ScopeContext context, SqlTupleElement sqlTupleElement) {
        SqlTupleDefinition sqlTupleDefinition =
                weave(context, sqlTupleElement);

        SqlTypeDictionaryDefinition typeHandlerDictionary = context.getDefaultSqlTypeDictionary();

        if (typeHandlerDictionary == null) {
            throw new IllegalArgumentException("Type handler dictionary hasn't been set");
        }

        SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition =
                sqlTupleDefinition.resolve(typeHandlerDictionary);

        context.getUnit().getCompilation().register(new TypeInformation(
                context.getUnit(),
                tupleRecordDefinition.getRecordDefinition().getQualifiedName(),
                tupleRecordDefinition.getSqlTypeDictionaryDefinition() == null ?
                        Collections.emptyList() :
                        Collections.singletonList(
                                tupleRecordDefinition.getSqlTypeDictionaryDefinition().getQualifiedName()
                        ),
                tupleRecordDefinition
        ));
    }

    private void process(ScopeContext context, SqlTypeDictionaryElement element) {
        SqlTypeDictionaryDefinition sqlTypeDictionaryDefinition =
                weave(context, element);

        String typeName = sqlTypeDictionaryDefinition.getPackageName() + '.' +
                sqlTypeDictionaryDefinition.getName();

        context.getUnit().getCompilation().register(new TypeInformation(
                context.getUnit(),
                typeName,
                Collections.emptyList(),
                sqlTypeDictionaryDefinition
        ));
    }

    private void process(ScopeContext context, DeclarationElement declarationElement) {
        switch (declarationElement.getTopic()) {
            case "default-sql-type-dict": {
                if (!(declarationElement.getValue() instanceof JavaReferenceElement)) {
                    throw new IllegalStateException("[current type handler dictionary] is not a java reference");
                }

                String typeHandlerDictionaryTypeName =
                        context.resolve((JavaReferenceElement) declarationElement.getValue()).stream()
                                .collect(Collectors.joining("."));

                TypeInformation typeInformation =
                        context.getUnit().getCompilation().lookup(typeHandlerDictionaryTypeName);

                if (typeInformation == null) {
                    throw new IllegalStateException("Cannot find " + typeHandlerDictionaryTypeName);
                }

                if (typeInformation.getCategory() != TypeInformation.Category.TYPE_HANDLER_DICTIONARY) {
                    throw new IllegalStateException(declarationElement.getValue() + " is not a type handler dictionary");
                }

                context.setDefaultTypeDictionary(typeInformation.getTypeHandlerDictionaryDefinition());
            }
            break;
            default:
                throw new IllegalStateException("Unknown declaration: " + declarationElement.getTopic());
        }
    }

    private void process(ScopeContext context, UtilizationElement utilizationElement) {
        context.registerLocalName(
                utilizationElement.getInternalName(),
                utilizationElement.getExternalReference().getNames()
        );
    }

    private SqlTupleDefinition weave(ScopeContext parentContext, SqlTupleElement sqlTupleElement) {
        return new SqlTupleDefinition(
                sqlTupleElement.getName(),
                parentContext.getCurrentPackage().stream().collect(Collectors.joining(".")),
                sqlTupleElement.getFields().stream()
                        .map(this::weave)
                        .collect(Collectors.toList())
        );
    }

    private SqlColumnDefinition weave(ColumnElement columnElement) {
        return new SqlColumnDefinition(
                columnElement.getColumnName(),
                new SqlTypeDetonator(columnElement.getSqlTypeElement().getRoot()),
                columnElement.getSqlTypeElement().getPrecision(),
                null,
                null,
                null,
                null
        );
    }

    private SqlTypeDetonator weave(SqlTypeElement sqlTypeElement) {
        return new SqlTypeDetonator(
                sqlTypeElement.getRoot()
        );
    }

    private SqlTypeDictionaryDefinition weave(
            ScopeContext parentContext,
            SqlTypeDictionaryElement element
    ) {
        HashMap<String, SqlTypeHandlerDeclaration.Builder> typeHandlerBuilders = new HashMap<>();

        int i = 0;

        for (SqlTypeDictionaryItemElement item : element.getItems()) {
            String name;
            String type;
            SqlTypeHandlerDeclaration.Builder builder;

            if (item.getTypeReference().isPresent()) {
                List<String> typeNameComponents = parentContext.resolve(item.getTypeReference().get());

                name = item.getHandlerName()
                        .orElseGet(() -> typeNameComponents.get(typeNameComponents.size() - 1));

                type = typeNameComponents.stream().collect(Collectors.joining("."));
                builder = SqlTypeHandlerDeclaration.builder()
                        .setName(name)
                        .setType(type);
                SqlTypeHandlerDeclaration.Builder currentBuilder = typeHandlerBuilders.putIfAbsent(
                        name,
                        builder
                );

                if (currentBuilder != null) {
                    if (!Objects.equals(currentBuilder.getType(), type)) {
                        throw new IllegalArgumentException("Duplicated name: " + name);
                    }

                    builder = currentBuilder;
                }
            } else {
                name = item.getHandlerName().orElseThrow(IllegalStateException::new);
                builder = Optional.ofNullable(typeHandlerBuilders.get(name))
                        .orElseThrow(() -> new IllegalArgumentException("Unknown type handler: " + name));
                type = builder.getType();
            }

            for (SqlTypePatternElement pattern : item.getPatterns()) {
                Object parameterJdbcType = pattern.getParameters().get("jdbcType");

                if (parameterJdbcType != null && !(parameterJdbcType instanceof String)) {
                    throw new IllegalArgumentException("jdbcType should be string");
                }

                int jdbcSqlType;

                {
                    String jdbcTypeName;
                    if (parameterJdbcType == null) {
                        jdbcTypeName = pattern.getSqlType().stream()
                                .map(String::toUpperCase)
                                .collect(Collectors.joining("_"));
                    } else {
                        jdbcTypeName = (String) parameterJdbcType;
                    }

                    try {
                        jdbcSqlType = JDBCType.valueOf(jdbcTypeName).getVendorTypeNumber();
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Failed to find jdbc type: " + jdbcTypeName);
                    }
                }

                builder.addTypeSelector(new SqlTypeHandlerDeclaration.TypeSelector(
                        new SqlTypeDetonator(pattern.getSqlType()),
                        jdbcSqlType,
                        i++
                ));
            }
        }

        return new SqlTypeDictionaryDefinition(
                element.getName(),
                parentContext.getCurrentPackage().stream().collect(Collectors.joining(".")),
                typeHandlerBuilders.values().stream()
                        .map(SqlTypeHandlerDeclaration.Builder::build)
                        .collect(Collectors.toList())
        );
    }
}
