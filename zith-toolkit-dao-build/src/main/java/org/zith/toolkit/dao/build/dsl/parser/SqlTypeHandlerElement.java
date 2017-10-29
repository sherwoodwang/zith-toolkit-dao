package org.zith.toolkit.dao.build.dsl.parser;

public class SqlTypeHandlerElement {

    private final JavaReferenceElement typeReference;
    private final String name;

    public SqlTypeHandlerElement(JavaReferenceElement typeReference, String name) {
        this.typeReference = typeReference;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JavaReferenceElement getTypeReference() {
        return typeReference;
    }
}
