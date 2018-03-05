package org.zith.toolkit.dao.build.dsl.element;

import java.util.Objects;

public class UtilizationElement {
    private final JavaReferenceElement externalReference;
    private final String internalName;

    public UtilizationElement(JavaReferenceElement externalReference, String internalName) {
        this.externalReference = Objects.requireNonNull(externalReference);
        this.internalName = internalName;
    }

    public JavaReferenceElement getExternalReference() {
        return externalReference;
    }

    public String getInternalName() {
        return internalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtilizationElement that = (UtilizationElement) o;
        return Objects.equals(externalReference, that.externalReference) &&
                Objects.equals(internalName, that.internalName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(externalReference, internalName);
    }

    @Override
    public String toString() {
        return "UtilizationElement{" +
                "externalReference=" + externalReference +
                ", internalName='" + internalName + '\'' +
                '}';
    }
}
