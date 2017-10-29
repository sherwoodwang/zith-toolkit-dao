package org.zith.toolkit.dao.build.generator;

import com.squareup.javapoet.*;
import org.zith.toolkit.dao.build.data.SqlTypeHandlerDeclaration;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.element.Modifier;

@NotThreadSafe
class TypeHandlerDictionaryClassGenerator {

    private final SqlTypeDictionaryDefinition dictionaryDefinition;
    private ClassName selfClassName;
    private TypeSpec.Builder typeSpecBuilder;

    TypeHandlerDictionaryClassGenerator(SqlTypeDictionaryDefinition dictionaryDefinition) {
        this.dictionaryDefinition = dictionaryDefinition;
    }

    JavaFile generate() {
        selfClassName = ClassName.get(dictionaryDefinition.getPackageName(), dictionaryDefinition.getName());
        typeSpecBuilder = TypeSpec.interfaceBuilder(selfClassName).addModifiers(Modifier.PUBLIC);

        generateMethods();

        return JavaFile.builder(selfClassName.packageName(), typeSpecBuilder.build()).build();
    }

    private void generateMethods() {
        for (SqlTypeHandlerDeclaration handlerDeclaration : dictionaryDefinition.getSqlTypeHandlerDeclarations()) {
            typeSpecBuilder.addMethod(
                    MethodSpec.methodBuilder("handlerOf" + handlerDeclaration.getName())
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .addParameter(String.class, "sqlType")
                            .addParameter(TypeName.INT, "jdbcType")
                            .returns(ParameterizedTypeName.get(
                                    ClassName.get(DaoSqlTypeHandler.class),
                                    ClassName.bestGuess(handlerDeclaration.getType())
                            ))
                            .build()
            );
        }
    }
}
