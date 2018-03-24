package org.zith.toolkit.dao.build.dsl;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import org.zith.toolkit.dao.build.data.SqlColumnDefinition;
import org.zith.toolkit.dao.build.data.SqlTupleDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeHandlerDeclaration;
import org.zith.toolkit.dao.build.dsl.element.*;
import org.zith.toolkit.dao.build.dsl.parser.ParserException;
import org.zith.toolkit.dao.build.dsl.parser.RecordSchemaParser;
import org.zith.toolkit.dao.build.generator.DaoSourceGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
                columnElement.getTypeHandlerName(),
                null,
                null,
                null
        );
    }

    private SqlTypeDictionaryDefinition weave(
            ScopeContext parentContext,
            SqlTypeDictionaryElement element
    ) {
        return new SqlTypeDictionaryDefinition(
                element.getName(),
                parentContext.getCurrentPackage().stream().collect(Collectors.joining(".")),
                element.getItems().stream()
                        .map(item -> new SqlTypeHandlerDeclaration(
                                item.getHandlerName().orElseGet(() -> {
                                    List<String> names = item.getType().getNames();
                                    return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE)
                                            .convert(names.get(names.size() - 1));
                                }),
                                parentContext.resolve(item.getType()).stream()
                                        .collect(Collectors.joining("."))
                        ))
                        .collect(Collectors.toList())
        );
    }

}
