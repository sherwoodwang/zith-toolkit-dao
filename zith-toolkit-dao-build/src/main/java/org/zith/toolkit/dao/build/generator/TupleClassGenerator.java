package org.zith.toolkit.dao.build.generator;

import com.squareup.javapoet.*;
import org.zith.toolkit.dao.build.data.SqlColumnDefinition;
import org.zith.toolkit.dao.build.data.SqlTupleDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeDetonator;
import org.zith.toolkit.dao.build.data.SqlTypeHandlerDeclaration;
import org.zith.toolkit.dao.support.DaoSqlColumn;
import org.zith.toolkit.dao.support.DaoSqlTupleType;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@NotThreadSafe
class TupleClassGenerator extends RecordClassGenerator {

    private final SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition;
    private TypeSpec.Builder tupleType;
    private ClassName tupleTypeName;
    private ParameterSpec sqlTypeDictionary;
    private CodeBlock.Builder constructorCode;
    private ParameterizedTypeName columnsBaseTypeName;
    private ClassName columnsTypeName;
    private HashMap<TypeHandlerInstanceKey, TypeHandlerInstanceState> typeHandlerStates;
    private FieldSpec columns;
    private ParameterSpec resultSet;
    private CodeBlock.Builder loadCode;

    TupleClassGenerator(SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition) {
        super(tupleRecordDefinition.getRecordDefinition());
        this.tupleRecordDefinition = tupleRecordDefinition;
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
                                .addParameter(getSelfClassName(), "record")
                                .addParameter(resultSet)
                                .addException(SQLException.class)
                                .returns(TypeName.VOID)
                                .addCode(loadCode.build())
                                .build())
                        .build());
        getTypeSpecBuilder().addMethod(
                MethodSpec.methodBuilder("daoRecordType")
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

            ClassName fieldType = ClassName.bestGuess(columnField.getFieldDefinition().getType());

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
                                            typeHandlerStates.get(createTypeHandlerInstanceKey(columnField))
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

        TypeHandlerInstanceKey typeHandlerInstanceKey =
                createTypeHandlerInstanceKey(columnFieldDefinition);

        int newTypeHandlerIndex = typeHandlerStates.size();

        TypeHandlerInstanceState typeHandlerInstanceState = typeHandlerStates.computeIfAbsent(
                typeHandlerInstanceKey,
                __ -> {
                    TypeHandlerInstanceState th = new TypeHandlerInstanceState();
                    th.key = typeHandlerInstanceKey;
                    th.declaration = typeHandler;
                    th.field =
                            FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                            ClassName.get(DaoSqlTypeHandler.class),
                                            ClassName.bestGuess(columnFieldDefinition.getFieldDefinition().getType())
                                    ),
                                    "th" + newTypeHandlerIndex
                            )
                                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                    .build();

                    ClassName jdbcSqlTypeClass = null;
                    String jdbcSqlTypeName = null;
                    CodeBlock jdbcSqlTypeReference;

                    for (Field field : Types.class.getFields()) {
                        try {
                            if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0 &&
                                    Objects.equals(field.get(null), columnFieldDefinition.getJdbcType())) {
                                jdbcSqlTypeClass = ClassName.get(Types.class);
                                jdbcSqlTypeName = field.getName();
                                break;
                            }
                        } catch (IllegalAccessException ignored) {
                        }
                    }

                    if (jdbcSqlTypeClass != null && jdbcSqlTypeName != null) {
                        jdbcSqlTypeReference =
                                CodeBlock.of("$T.$L", jdbcSqlTypeClass, jdbcSqlTypeName);
                    } else {
                        jdbcSqlTypeReference =
                                CodeBlock.of("$L", columnFieldDefinition.getJdbcType());
                    }

                    constructorCode.addStatement(
                            "$N = $N.$L($S, $L)",
                            th.field,
                            sqlTypeDictionary,
                            "handlerOf" + typeHandler.getName(),
                            columnFieldDefinition.getType().toString(),
                            jdbcSqlTypeReference
                    );

                    return th;
                });

        loadCode.addStatement(
                "record.$L($N.load($N, $L))",
                "set" + columnFieldDefinition.getFieldDefinition().getAccessorName(),
                typeHandlerInstanceState.field,
                resultSet,
                i
        );
    }

    private TypeHandlerInstanceKey createTypeHandlerInstanceKey(
            SqlColumnDefinition.ColumnFieldDefinition columnFieldDefinition) {
        return new TypeHandlerInstanceKey(
                columnFieldDefinition.getTypeHandler().getName(),
                columnFieldDefinition.getJdbcType(),
                columnFieldDefinition.getType()
        );
    }

    private class TypeHandlerInstanceKey {
        private final String name;
        private final int jdbcSqlType;
        private final SqlTypeDetonator rootSqlTypeName;

        private TypeHandlerInstanceKey(String name, int jdbcSqlType, SqlTypeDetonator rootSqlTypeName) {
            this.name = Objects.requireNonNull(name);
            this.jdbcSqlType = jdbcSqlType;
            this.rootSqlTypeName = Objects.requireNonNull(rootSqlTypeName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeHandlerInstanceKey that = (TypeHandlerInstanceKey) o;

            if (jdbcSqlType != that.jdbcSqlType) return false;
            if (!name.equals(that.name)) return false;
            return rootSqlTypeName.equals(that.rootSqlTypeName);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + jdbcSqlType;
            result = 31 * result + rootSqlTypeName.hashCode();
            return result;
        }
    }

    private class TypeHandlerInstanceState {
        private TypeHandlerInstanceKey key;
        private SqlTypeHandlerDeclaration declaration;
        private FieldSpec field;
    }

    private static class TypeHandlerFactor {

        public static int create(SqlTypeHandlerDeclaration.TypeSelector ts, int jdbcSqlType, List<String> rootSqlType) {
            return 0;
        }
    }
}