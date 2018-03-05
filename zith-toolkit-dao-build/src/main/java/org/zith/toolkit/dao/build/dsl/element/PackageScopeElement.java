package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class PackageScopeElement {
    private final List<String> packageNameComponents;
    private final List<BlockItemElement> elements;

    public PackageScopeElement(List<String> packageNameComponents, List<BlockItemElement> elements) {
        this.packageNameComponents = ImmutableList.copyOf(packageNameComponents);
        this.elements = ImmutableList.copyOf(elements);
    }

    public List<String> getPackageNameComponents() {
        return packageNameComponents;
    }

    public List<BlockItemElement> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return "PackageScopeElement{" +
                "packageNameComponents=" + packageNameComponents +
                ", elements=" + elements +
                '}';
    }
}
