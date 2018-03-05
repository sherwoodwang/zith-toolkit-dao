package org.zith.toolkit.dao.build.dsl.element;

import java.util.Objects;

public class ImportedPathElement {
    private final String path;

    public ImportedPathElement(String path) {
        this.path = Objects.requireNonNull(path);
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportedPathElement that = (ImportedPathElement) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "ImportedPathElement{" +
                "path='" + path + '\'' +
                '}';
    }
}
