package org.zith.toolkit.dao.build.dsl.parser;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JavaFarReferenceElement {
    private final int upper;
    private final List<String> names;

    public JavaFarReferenceElement(int upper, List<String> names) {
        this.upper = upper;
        this.names = ImmutableList.copyOf(names);
    }

    public int getUpper() {
        return upper;
    }

    public List<String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return upper == -1 ? "" : IntStream.range(0, upper).boxed().map(__ -> ".").collect(Collectors.joining()) +
                names.stream().collect(Collectors.joining("."));
    }
}
