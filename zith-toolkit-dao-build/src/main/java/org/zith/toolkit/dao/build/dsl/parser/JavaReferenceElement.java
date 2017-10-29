package org.zith.toolkit.dao.build.dsl.parser;

import com.google.common.collect.ImmutableList;

import java.util.List;
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
    public String toString() {
        return names.stream().collect(Collectors.joining("."));
    }
}
