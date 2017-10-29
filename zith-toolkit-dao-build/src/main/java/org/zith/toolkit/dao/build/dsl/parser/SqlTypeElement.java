package org.zith.toolkit.dao.build.dsl.parser;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public class SqlTypeElement {
    private final List<String> root;
    private final List<Integer> precision;

    SqlTypeElement(List<String> root) {
        this.root = ImmutableList.copyOf(root);
        precision = null;
    }

    SqlTypeElement(List<String> root, List<Integer> precision) {
        this.root = ImmutableList.copyOf(root);
        this.precision = ImmutableList.copyOf(precision);
    }

    public List<String> getRoot() {
        return root;
    }

    public List<Integer> getPrecision() {
        return precision;
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
