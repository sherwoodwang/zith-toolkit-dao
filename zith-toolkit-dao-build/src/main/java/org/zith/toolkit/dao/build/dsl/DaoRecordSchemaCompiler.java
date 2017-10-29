package org.zith.toolkit.dao.build.dsl;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.zith.toolkit.dao.build.data.*;
import org.zith.toolkit.dao.build.dsl.parser.*;
import org.zith.toolkit.dao.build.generator.DaoSourceGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.JDBCType;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

public class DaoRecordSchemaCompiler {

    private final DaoSourceGenerator daoSourceGenerator;

    public DaoRecordSchemaCompiler() {
        daoSourceGenerator = new DaoSourceGenerator();
    }

    public void compile(Collection<File> sources, File destroot) throws IOException {
        Compilation compilation = new Compilation(daoSourceGenerator, destroot);

        for (File source : sources) {
            compile(compilation.unit(source), CharStreams.fromPath(source.toPath()), true);
        }
    }

    private synchronized void compile(Compilation.Unit unit, CharStream input, boolean emitting) throws IOException {
        RecordSchemaLexer lexer = new RecordSchemaLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        RecordSchemaParser parser = new RecordSchemaParser(tokenStream);

        ScopeContext topLevelContext = new ScopeContext(unit, Collections.emptyList());

        RecordSchemaElement recordSchemaElement = parser.recordSchema().element;
        for (String path : recordSchemaElement.getImportationPaths()) {
            File importedFile = new File(unit.getFile().getParent(), path);

            compile(unit.getCompilation().unit(importedFile), CharStreams.fromPath(importedFile.toPath()), false);
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
                case RECORD_DEFINITION:
                    process(context, element.getTupleElement());
                    break;
                case DECLARATION:
                    process(context, element.getDeclarationElement());
                    break;
                case UTILIZATION:
                    process(context, element.getUtilizationElement());
                    break;
                case TYPE_HANDLER_DICTIONARY_DEFINITION:
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

    private void process(ScopeContext context, TupleElement tupleElement) {
        SqlTupleDefinition sqlTupleDefinition =
                weave(context, tupleElement);

        SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition =
                sqlTupleDefinition.resolve(context.getCurrentTypeHandlerDictionary());

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
            case "current-type-handler-dictionary": {
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

                context.setCurrentTypeHandlerDictionary(typeInformation.getTypeHandlerDictionaryDefinition());
            }
            break;
            default:
                throw new IllegalStateException("Unknown declaration: " + declarationElement.getTopic());
        }
    }

    private void process(ScopeContext context, UtilizationElement utilizationElement) {
        context.registerLocalName(
                utilizationElement.getInternalName(),
                context.resolve(utilizationElement.getExternalReference())
        );
    }

    private SqlTupleDefinition weave(ScopeContext parentContext, TupleElement tupleElement) {
        return new SqlTupleDefinition(
                tupleElement.getName(),
                parentContext.getCurrentPackage().stream().collect(Collectors.joining(".")),
                tupleElement.getFields().stream()
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
            switch (item.getType()) {
                case TYPE_HANDLER: {
                    SqlTypeHandlerElement typeHandlerElement = item.getSqlTypeHandlerElement();

                    String name = typeHandlerElement.getName();
                    List<String> type = parentContext.resolve(typeHandlerElement.getTypeReference());
                    if (name == null) {
                        name = type.get(type.size() - 1);
                    }

                    if (typeHandlerBuilders.putIfAbsent(
                            name,
                            SqlTypeHandlerDeclaration.builder()
                                    .setName(name)
                                    .setType(type.stream().collect(Collectors.joining(".")))
                    ) != null) {
                        throw new IllegalArgumentException("Duplicated name: " + name);
                    }
                }
                break;
                case TYPE_SELECTOR: {
                    SqlTypeSelectorElement typeSelectorDeclaration = item.getSqlTypeSelectorDeclaration();

                    String name = typeSelectorDeclaration.getTypeHandlerName();
                    SqlTypeHandlerDeclaration.Builder builder =
                            typeHandlerBuilders.get(name);

                    if (builder == null) {
                        throw new NoSuchElementException("Cannot find a type handler with name: " + name);
                    }

                    SqlTypePatternElement sqlTypePatternElement = typeSelectorDeclaration.getSqlTypePatternElement();

                    Object parameterJdbcType = sqlTypePatternElement.getParameters().get("jdbcType");

                    if (parameterJdbcType != null && !(parameterJdbcType instanceof String)) {
                        throw new IllegalArgumentException("jdbcType should be string");
                    }

                    int jdbcSqlType;

                    {
                        String jdbcTypeName;
                        if (parameterJdbcType == null) {
                            jdbcTypeName = typeSelectorDeclaration.getSqlTypePatternElement().getSqlType().stream()
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
                            new SqlTypeDetonator(sqlTypePatternElement.getSqlType()),
                            jdbcSqlType,
                            i++
                    ));
                }
                break;
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
