package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public class RecordSchemaHeadElement {
    private final List<ImportedPathElement> paths;

    public RecordSchemaHeadElement(List<ImportedPathElement> paths) {
        this.paths = ImmutableList.copyOf(paths);
    }

    public List<ImportedPathElement> getPaths() {
        return paths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordSchemaHeadElement that = (RecordSchemaHeadElement) o;
        return Objects.equals(paths, that.paths);
    }

    @Override
    public int hashCode() {

        return Objects.hash(paths);
    }

    @Override
    public String toString() {
        return "RecordSchemaHeadElement{" +
                "paths=" + paths +
                '}';
    }
}
