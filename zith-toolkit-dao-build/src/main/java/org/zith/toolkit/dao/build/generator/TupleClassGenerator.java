package org.zith.toolkit.dao.build.generator;

import com.squareup.javapoet.*;
import org.zith.toolkit.dao.build.data.SqlColumnDefinition;
import org.zith.toolkit.dao.build.data.SqlTupleDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeHandlerDeclaration;
import org.zith.toolkit.dao.support.DaoSqlColumn;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTupleType;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.element.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@NotThreadSafe
public class TupleClassGenerator extends RecordClassGenerator {

    private SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition;
    private TypeSpec.Builder tupleType;
    private ClassName tupleTypeName;
    private ParameterSpec sqlTypeDictionary;
    private CodeBlock.Builder constructorCode;
    private ParameterizedTypeName columnsBaseTypeName;
    private ClassName columnsTypeName;
    private HashMap<String, TypeHandlerInstanceState> typeHandlerStates;
    private FieldSpec columns;
    private ParameterSpec resultSet;
    private CodeBlock.Builder loadCode;

    @SuppressWarnings("WeakerAccess")
    public TupleClassGenerator() {
        super();
    }

    public final JavaFile generate(SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition) {
        return generateFromTupleRecord(tupleRecordDefinition);
    }

    @SuppressWarnings("WeakerAccess")
    JavaFile generateFromTupleRecord(SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition) {
        this.tupleRecordDefinition = tupleRecordDefinition;
        return generateFromRecord(tupleRecordDefinition.getRecordDefinition());
    }

    @Override
    protected void generateOtherBeanClassComponents() {
        super.generateOtherBeanClassComponents();

        tupleTypeName = getSelfClassName().nestedClass("TupleType");
        tupleType = TypeSpec.classBuilder(tupleTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(DaoSqlTupleType.class), getSelfClassName()));

        sqlTypeDictionary = ParameterSpec
                .builder(
                        ClassName.bestGuess(
                                tupleRecordDefinition.getSqlTypeDictionaryDefinition().getQualifiedName()
                        ),
                        "sqlTypeDictionary"
                )
                .build();

        constructorCode = CodeBlock.builder();
        typeHandlerStates = new HashMap<>();

        columnsBaseTypeName = ParameterizedTypeName.get(
                ClassName.get(DaoSqlTupleType.Columns.class),
                getSelfClassName()
        );
        columnsTypeName = tupleTypeName.nestedClass("Columns");
        columns = FieldSpec.builder(columnsTypeName, "columns")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        resultSet = ParameterSpec.builder(ResultSet.class, "resultSet").build();
        loadCode = CodeBlock.builder();

        int i = 1;
        for (SqlColumnDefinition.ColumnFieldDefinition columnFieldDefinition :
                tupleRecordDefinition.getColumnFieldDefinitions()) {
            generateFieldLoader(i++, columnFieldDefinition);
        }

        generateColumns();

        getTypeSpecBuilder().addType(
                tupleType
                        .addFields(
                                typeHandlerStates.values().stream()
                                        .map(th -> th.field)
                                        .collect(Collectors.toList())
                        )
                        .addField(columns)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(sqlTypeDictionary)
                                .addCode(constructorCode.build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("create")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override.class)
                                .returns(getSelfClassName())
                                .addStatement("return new $T()", getSelfClassName())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("set")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override.class)
                                .addParameter(getSelfClassName(), "to")
                                .addParameter(getSelfClassName(), "from")
                                .returns(TypeName.VOID)
                                .addStatement("from.set(to)")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("columns")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override.class)
                                .returns(columnsTypeName)
                                .addStatement("return $N", columns)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("load")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override.class)
                                .addParameter(ClassName.get(DaoSqlOperationContext.class), "context")
                                .addParameter(getSelfClassName(), "record")
                                .addParameter(resultSet)
                                .addException(SQLException.class)
                                .returns(TypeName.VOID)
                                .addCode(loadCode.build())
                                .build())
                        .build());
        getTypeSpecBuilder().addMethod(
                MethodSpec.methodBuilder("tupleType")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(sqlTypeDictionary)
                        .returns(tupleTypeName)
                        .addStatement("return new $T($N)", tupleTypeName, sqlTypeDictionary)
                        .build());
    }

    private void generateColumns() {
        TypeSpec.Builder columnsType = TypeSpec.classBuilder(columnsTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(columnsBaseTypeName);

        CodeBlock.Builder iAll = CodeBlock.builder();

        int numberOfFields = tupleRecordDefinition.getColumnFieldDefinitions().size();
        if (numberOfFields == 0) {
            iAll.add("$T.emptyList(", Collections.class);
        } else {
            iAll.add("$T.unmodifiableList(", Collections.class);

            if (numberOfFields == 1) {
                iAll.add("$T.singletonList(", Collections.class);
            } else {
                iAll.add("$T.asList(", Arrays.class);
            }
        }

        boolean first = true;
        for (SqlColumnDefinition.ColumnFieldDefinition columnField :
                tupleRecordDefinition.getColumnFieldDefinitions()) {

            if (!first) {
                iAll.add(", ");
            }

            TypeName fieldType = getFieldDescriptor(columnField.getFieldDefinition().getFieldName()).getTypeName();

            ParameterizedTypeName columnFieldType = ParameterizedTypeName.get(
                    ClassName.get(DaoSqlColumn.class),
                    getSelfClassName(),
                    fieldType
            );

            ParameterSpec pRecordOfSet = ParameterSpec
                    .builder(getSelfClassName(), "record")
                    .build();
            ParameterSpec pValueOfSet = ParameterSpec
                    .builder(fieldType, "value")
                    .build();

            FieldSpec field = FieldSpec
                    .builder(
                            columnFieldType,
                            "f" + columnField.getFieldDefinition().getMemberName(),
                            Modifier.PRIVATE, Modifier.FINAL
                    )
                    .initializer("$L", TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(columnFieldType)
                            .addMethod(MethodSpec.methodBuilder("getColumnName")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addAnnotation(Override.class)
                                    .returns(String.class)
                                    .addStatement("return $S", columnField.getColumn())
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("getFieldName")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addAnnotation(Override.class)
                                    .returns(String.class)
                                    .addStatement("return $S", columnField.getFieldDefinition().getFieldName())
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("getTypeHandler")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addAnnotation(Override.class)
                                    .returns(
                                            ParameterizedTypeName.get(ClassName.get(DaoSqlTypeHandler.class),
                                                    fieldType
                                            ))
                                    .addStatement("return $N",
                                            typeHandlerStates.get(columnField.getTypeHandler().getName())
                                                    .field)
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("set")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addAnnotation(Override.class)
                                    .returns(TypeName.VOID)
                                    .addParameter(pRecordOfSet)
                                    .addParameter(pValueOfSet)
                                    .addStatement("$N.$L($N)",
                                            pRecordOfSet,
                                            "set" + columnField.getFieldDefinition().getAccessorName(),
                                            pValueOfSet)
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("get")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addAnnotation(Override.class)
                                    .returns(fieldType)
                                    .addParameter(pRecordOfSet)
                                    .addStatement("return $N.$L()",
                                            pRecordOfSet,
                                            "get" + columnField.getFieldDefinition().getAccessorName())
                                    .build())
                            .build())
                    .build();

            columnsType.addField(field);
            columnsType.addMethod(MethodSpec
                    .methodBuilder("get" + columnField.getFieldDefinition().getAccessorName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(columnFieldType)
                    .addStatement("return $N", field)
                    .build());

            iAll.add("$N", field);

            first = false;
        }

        if (numberOfFields > 0) {
            iAll.add(")");
        }
        iAll.add(")");

        {
            ParameterizedTypeName tAll = ParameterizedTypeName.get(
                    ClassName.get(List.class),
                    ParameterizedTypeName.get(
                            ClassName.get(DaoSqlColumn.class),
                            getSelfClassName(),
                            WildcardTypeName.subtypeOf(ClassName.get(Object.class))
                    )
            );

            FieldSpec fAll = FieldSpec
                    .builder(tAll, "d_all", Modifier.PRIVATE, Modifier.FINAL)
                    .initializer(iAll.build())
                    .build();

            columnsType.addField(fAll);

            columnsType.addMethod(MethodSpec
                    .methodBuilder("all")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .returns(tAll)
                    .addStatement("return $N", fAll)
                    .build());
        }

        constructorCode.addStatement(
                "$N = new $T()",
                this.columns,
                columnsTypeName
        );

        tupleType.addType(columnsType.build());
    }

    private void generateFieldLoader(int i, SqlColumnDefinition.ColumnFieldDefinition columnFieldDefinition) {
        SqlTypeHandlerDeclaration typeHandler = columnFieldDefinition.getTypeHandler();

        int newTypeHandlerIndex = typeHandlerStates.size();

        TypeHandlerInstanceState typeHandlerInstanceState = typeHandlerStates.computeIfAbsent(
                columnFieldDefinition.getTypeHandler().getName(),
                __ -> {
                    TypeHandlerInstanceState th = new TypeHandlerInstanceState();
                    th.declaration = typeHandler;
                    th.field =
                            FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                            ClassName.get(DaoSqlTypeHandler.class),
                                            getFieldDescriptor(
                                                    columnFieldDefinition
                                                            .getFieldDefinition()
                                                            .getFieldName()
                                            )
                                                    .getTypeName()
                                    ),
                                    "th" + newTypeHandlerIndex
                            )
                                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                    .build();

                    constructorCode.addStatement(
                            "$N = $N.$L()",
                            th.field,
                            sqlTypeDictionary,
                            TypeDictionaryClassGenerator.handlerGetterName(typeHandler)
                    );

                    return th;
                });

        loadCode.addStatement(
                "record.$L($N.load($N, $N, $L))",
                "set" + columnFieldDefinition.getFieldDefinition().getAccessorName(),
                typeHandlerInstanceState.field,
                "context",
                resultSet,
                i
        );
    }

    private class TypeHandlerInstanceState {
        private SqlTypeHandlerDeclaration declaration;
        private FieldSpec field;
    }

}