package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlTypeElement {
    private final List<String> root;
    private final List<Integer> precision;

    public SqlTypeElement(List<String> root) {
        this(root, null);
    }

    public SqlTypeElement(List<String> root, List<Integer> precision) {
        this.root = ImmutableList.copyOf(root);
        this.precision = precision == null ? null : ImmutableList.copyOf(precision);
    }

    public List<String> getRoot() {
        return root;
    }

    public List<Integer> getPrecision() {
        return precision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTypeElement that = (SqlTypeElement) o;
        return Objects.equals(root, that.root) &&
                Objects.equals(precision, that.precision);
    }

    @Override
    public int hashCode() {

        return Objects.hash(root, precision);
    }

    @Override
    public String toString() {
        return root.stream().collect(Collectors.joining(" ")) + (precision == null ?
                "" :
                precision.stream()
                        .map(i -> Integer.toString(i))
                        .collect(Collectors.joining(", ", "(", ")"))
        );
    }
}
