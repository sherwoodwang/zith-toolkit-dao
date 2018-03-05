package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavaReferenceElement {
    private final List<String> names;

    public JavaReferenceElement(List<String> names) {
        if (names.size() < 1) {
            throw new IllegalArgumentException();
        }

        this.names = ImmutableList.copyOf(names);
    }

    public List<String> getNames() {
        return names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaReferenceElement that = (JavaReferenceElement) o;
        return Objects.equals(names, that.names);
    }

    @Override
    public int hashCode() {

        return Objects.hash(names);
    }

    @Override
    public String toString() {
        return "JavaReferenceElement{" +
                "names=" + names +
                '}';
    }
}
