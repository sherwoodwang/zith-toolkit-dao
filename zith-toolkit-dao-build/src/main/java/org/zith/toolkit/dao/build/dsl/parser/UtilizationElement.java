package org.zith.toolkit.dao.build.dsl.parser;

public class UtilizationElement {
    private final JavaFarReferenceElement externalReference;
    private final String internalName;

    public UtilizationElement(JavaFarReferenceElement externalReference, String internalName) {
        this.externalReference = externalReference;
        this.internalName = internalName;
    }

    public JavaFarReferenceElement getExternalReference() {
        return externalReference;
    }

    public String getInternalName() {
        return internalName;
    }
}
