package org.zith.toolkit.dao.build.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.*;
import org.zith.toolkit.dao.build.data.FieldDefinition;
import org.zith.toolkit.dao.build.data.RecordDefinition;

import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import java.util.*;

@NotThreadSafe
class RecordClassGenerator {
    private final RecordDefinition recordDefinition;
    private ClassName selfClassName;
    private TypeSpec.Builder typeSpecBuilder;
    private List<FieldDescriptor> fieldDescriptors;

    RecordClassGenerator(RecordDefinition recordDefinition) {
        this.recordDefinition = recordDefinition;
    }

    JavaFile generate() {
        selfClassName = ClassName.get(recordDefinition.getPackageName(), recordDefinition.getName());
        typeSpecBuilder = TypeSpec.classBuilder(selfClassName).addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        createFieldStates(recordDefinition);
        generateMemberFields();
        generateDefaultConstructor();
        generateCopyConstructor();
        generateAssigner();
        generateEqualsMethod();
        generateHashCodeMethod();
        generateToStringMethod();
        generateGettersAndSetters();
        generateBeanMap();
        generateOtherBeanClassComponents();

        return JavaFile.builder(selfClassName.packageName(), typeSpecBuilder.build()).build();
    }

    protected final ClassName getSelfClassName() {
        return selfClassName;
    }

    protected final TypeSpec.Builder getTypeSpecBuilder() {
        return typeSpecBuilder;
    }

    private void createFieldStates(RecordDefinition recordDefinition) {
        fieldDescriptors = recordDefinition.getFields().stream()
                .map(FieldDescriptor::new)
                .collect(ImmutableList.toImmutableList());
    }

    private void generateMemberFields() {
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            typeSpecBuilder.addField(
                    FieldSpec.builder(
                            fieldDescriptor.getTypeName(),
                            fieldDescriptor.getFieldDefinition().getMemberName())
                            .addModifiers(Modifier.PRIVATE)
                            .build()
            );
        }
    }

    private void generateDefaultConstructor() {
        typeSpecBuilder.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .build()
        );
    }

    private void generateCopyConstructor() {
        typeSpecBuilder.addMethod(
                MethodSpec.constructorBuilder()
                        .addParameter(selfClassName, "a")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("set(a)")
                        .build()
        );
    }

    private void generateAssigner() {
        MethodSpec.Builder copySetter = MethodSpec.methodBuilder("set")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(selfClassName, "a");

        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            copySetter.addStatement("this.$L = a.$L",
                    fieldDescriptor.getFieldDefinition().getMemberName(), fieldDescriptor.getFieldDefinition().getMemberName());
        }

        typeSpecBuilder.addMethod(copySetter.build());
    }

    private void generateEqualsMethod() {
        MethodSpec.Builder equalsMethod = MethodSpec.methodBuilder("equals")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Object.class, "o")
                .returns(TypeName.BOOLEAN)
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addStatement("$T that = ($T) o", selfClassName, selfClassName);

        for (FieldDescriptor fieldDefinition : fieldDescriptors) {
            equalsMethod.addStatement(
                    "if ($L != null ? !$L.equals(that.$L) : that.$L != null) return false",
                    fieldDefinition.getFieldDefinition().getMemberName(),
                    fieldDefinition.getFieldDefinition().getMemberName(),
                    fieldDefinition.getFieldDefinition().getMemberName(),
                    fieldDefinition.getFieldDefinition().getMemberName()
            );
        }

        equalsMethod.addStatement("return true");

        typeSpecBuilder.addMethod(equalsMethod.build());
    }

    private void generateHashCodeMethod() {
        MethodSpec.Builder hashCodeMethod = MethodSpec.methodBuilder("hashCode")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT);

        boolean first = true;
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            if (first) {
                hashCodeMethod.addCode("int result = ");
            } else {
                hashCodeMethod.addCode("result = 31 * result + (");
            }

            hashCodeMethod.addCode(
                    "$L != null ? $L.hashCode() : 0",
                    fieldDescriptor.getFieldDefinition().getMemberName(),
                    fieldDescriptor.getFieldDefinition().getMemberName()
            );

            if (first) {
                hashCodeMethod.addCode(";\n");
            } else {
                hashCodeMethod.addCode(");\n");
            }

            first = false;
        }

        hashCodeMethod.addStatement("return result");

        typeSpecBuilder.addMethod(hashCodeMethod.build());
    }

    private void generateToStringMethod() {
        MethodSpec.Builder toStringMethod = MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class);

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        codeBlockBuilder.add("return $S +\n", recordDefinition.getName() + '{');
        codeBlockBuilder.indent();

        boolean first = true;
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            String segment;
            if (first) {
                segment = fieldDescriptor.getFieldDefinition().getFieldName() + "=";
            } else {
                segment = ", " + fieldDescriptor.getFieldDefinition().getFieldName() + "=";
            }
            codeBlockBuilder.add("$S + $L +\n", segment, fieldDescriptor.getFieldDefinition().getMemberName());

            first = false;
        }

        codeBlockBuilder.add("$S;\n", "}");
        codeBlockBuilder.unindent();

        toStringMethod.addCode(codeBlockBuilder.build());
        typeSpecBuilder.addMethod(toStringMethod.build());
    }

    private void generateGettersAndSetters() {
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            FieldDefinition fieldDefinition = fieldDescriptor.getFieldDefinition();

            typeSpecBuilder.addMethod(
                    MethodSpec.methodBuilder("get" + fieldDefinition.getAccessorName())
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("return $L", fieldDefinition.getMemberName())
                            .returns(fieldDescriptor.getTypeName())
                            .build()
            );

            String parameterName = forName(fieldDefinition.getFieldName());
            typeSpecBuilder.addMethod(
                    MethodSpec.methodBuilder("set" + fieldDefinition.getAccessorName())
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(fieldDescriptor.getTypeName(), parameterName)
                            .addStatement("this.$L = $L",
                                    fieldDefinition.getMemberName(), parameterName)
                            .returns(TypeName.VOID)
                            .build()
            );
        }
    }

    private void generateBeanMap() {
        ClassName sqlValueMapClassName = selfClassName.nestedClass("BeanMap");

        CodeBlock.Builder constructorCode = CodeBlock.builder();
        constructorCode.addNamed(
                "entrySet = $tCollections:T.unmodifiableSet(new $tHashSet:T<>($tArrays:T.asList(",
                ImmutableMap.<String, Object>builder()
                        .put("tCollections", Collections.class)
                        .put("tHashSet", HashSet.class)
                        .put("tArrays", Arrays.class)
                        .build());
        constructorCode.indent();
        boolean first = true;

        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            FieldDefinition fieldDefinition = fieldDescriptor.getFieldDefinition();

            if (first) {
                constructorCode.add("\n");
            } else {
                constructorCode.add(",\n");
            }

            ParameterSpec setValueParameter =
                    ParameterSpec.builder(Object.class, forName(fieldDefinition.getFieldName()))
                            .build();

            constructorCode.add(
                    "$L",
                    TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(ParameterizedTypeName
                                    .get(Map.Entry.class, String.class, Object.class))
                            .addMethod(MethodSpec.methodBuilder("getKey")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addAnnotation(Override.class)
                                    .returns(String.class)
                                    .addStatement("return $S", fieldDefinition.getFieldName())
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("getValue")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addAnnotation(Override.class)
                                    .returns(Object.class)
                                    .addStatement("return $T.this.$L",
                                            selfClassName, fieldDefinition.getMemberName())
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("setValue")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addAnnotation(Override.class)
                                    .addParameter(setValueParameter)
                                    .returns(Object.class)
                                    .addStatement(
                                            "$T $L = $T.this.$L",
                                            fieldDescriptor.getTypeName(),
                                            "oldValue",
                                            selfClassName,
                                            fieldDefinition.getMemberName()
                                    )
                                    .addStatement(
                                            "$T.this.$L = ($T) $N",
                                            selfClassName,
                                            fieldDefinition.getMemberName(),
                                            fieldDescriptor.getTypeName(),
                                            setValueParameter
                                    )
                                    .addStatement("return oldValue")
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("hashCode")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addAnnotation(Override.class)
                                    .returns(TypeName.INT)
                                    .addStatement(
                                            "return $S.hashCode() ^ $T.hashCode($T.this.$L)",
                                            fieldDefinition.getFieldName(),
                                            Objects.class,
                                            selfClassName,
                                            fieldDefinition.getMemberName()
                                    )
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("equals")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addAnnotation(Override.class)
                                    .addParameter(Object.class, "o")
                                    .returns(TypeName.BOOLEAN)
                                    .addStatement("if (this == o) return true")
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("if (o instanceof $T)", Map.Entry.class)
                                            .addNamed(
                                                    "$tEntry:T e = ($tEntry:T) o;\n",
                                                    ImmutableMap.<String, Object>builder()
                                                            .put(
                                                                    "tEntry",
                                                                    ParameterizedTypeName.get(
                                                                            ClassName.get(Map.Entry.class),
                                                                            WildcardTypeName.subtypeOf(Object.class),
                                                                            WildcardTypeName.subtypeOf(Object.class)))
                                                            .build()
                                            )
                                            .addStatement(
                                                    "return $S.equals(e.getKey()) &&" +
                                                            " $T.equals($T.this.$L, e.getValue())",
                                                    fieldDefinition.getFieldName(),
                                                    Objects.class,
                                                    selfClassName,
                                                    fieldDefinition.getMemberName()
                                            )
                                            .endControlFlow()
                                            .addStatement("return false")
                                            .build())
                                    .build())
                            .build()
            );

            first = false;
        }

        constructorCode.unindent();
        constructorCode.add("\n)));\n");

        ParameterizedTypeName entrySetTypeName = ParameterizedTypeName.get(
                ClassName.get(Set.class),
                ParameterizedTypeName.get(Map.Entry.class, String.class, Object.class));

        TypeSpec.Builder sqlValueMapTypeSpecBuilder =
                TypeSpec.classBuilder(sqlValueMapClassName)
                        .superclass(ParameterizedTypeName.get(AbstractMap.class, String.class, Object.class))
                        .addModifiers(Modifier.PRIVATE)
                        .addField(entrySetTypeName, "entrySet", Modifier.PRIVATE, Modifier.FINAL)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addCode(constructorCode.build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("entrySet")
                                .addModifiers(Modifier.PUBLIC)
                                .addAnnotation(Override.class)
                                .returns(entrySetTypeName)
                                .addStatement("return entrySet")
                                .build());

        typeSpecBuilder.addType(sqlValueMapTypeSpecBuilder.build());

        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("asMap")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(Map.class, String.class, Object.class))
                .addStatement("return new $T()", sqlValueMapClassName)
                .build());
    }

    protected void generateOtherBeanClassComponents() {
    }

    private String forName(String fieldName) {
        String name = null;

        if (SourceVersion.isName(fieldName)) {
            name = fieldName;
        } else if (SourceVersion.isName(fieldName + "_")) {
            name = fieldName + "_";
        } else if (SourceVersion.isName("_" + fieldName)) {
            name = "_" + fieldName;
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot convert " + fieldName + " to a valid Java name");
        }

        return name;
    }

    protected static class FieldDescriptor {

        private final FieldDefinition fieldDefinition;
        private final ClassName typeName;

        FieldDescriptor(FieldDefinition fieldDefinition) {
            this.fieldDefinition = fieldDefinition;
            this.typeName = ClassName.bestGuess(fieldDefinition.getType());
        }

        public FieldDefinition getFieldDefinition() {
            return fieldDefinition;
        }

        public ClassName getTypeName() {
            return typeName;
        }
    }
}
