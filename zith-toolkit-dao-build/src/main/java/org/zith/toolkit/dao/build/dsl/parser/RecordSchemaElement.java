package org.zith.toolkit.dao.build.dsl.parser;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class RecordSchemaElement {
    private final List<String> importationPaths;
    private final List<BlockItemElement> elements;

    public RecordSchemaElement(List<String> importationPaths, List<BlockItemElement> elements) {
        this.importationPaths = ImmutableList.copyOf(importationPaths);
        this.elements = ImmutableList.copyOf(elements);
    }

    public List<String> getImportationPaths() {
        return importationPaths;
    }

    public List<BlockItemElement> getElements() {
        return elements;
    }
}
