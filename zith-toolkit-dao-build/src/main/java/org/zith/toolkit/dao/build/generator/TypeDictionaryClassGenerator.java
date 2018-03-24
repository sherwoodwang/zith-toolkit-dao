package org.zith.toolkit.dao.build.generator;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.squareup.javapoet.*;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeHandlerDeclaration;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import javax.annotation.concurrent.NotThreadSafe;
import javax.lang.model.element.Modifier;

@NotThreadSafe
class TypeDictionaryClassGenerator {
    private static final Converter<String, String> HANDLER_NAME_CONVERTER =
            CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL);

    private final SqlTypeDictionaryDefinition dictionaryDefinition;
    private ClassName selfClassName;
    private TypeSpec.Builder typeSpecBuilder;

    TypeDictionaryClassGenerator(SqlTypeDictionaryDefinition dictionaryDefinition) {
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
                    MethodSpec.methodBuilder(handlerGetterName(handlerDeclaration))
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(ParameterizedTypeName.get(
                                    ClassName.get(DaoSqlTypeHandler.class),
                                    ClassName.bestGuess(handlerDeclaration.getType())
                            ))
                            .build()
            );
        }
    }

    static String handlerGetterName(SqlTypeHandlerDeclaration handlerDeclaration) {
        return "handlerOf" + HANDLER_NAME_CONVERTER.convert(handlerDeclaration.getName());
    }
}
