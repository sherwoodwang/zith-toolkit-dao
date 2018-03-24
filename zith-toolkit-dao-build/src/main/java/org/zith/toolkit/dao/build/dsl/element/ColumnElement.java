package org.zith.toolkit.dao.build.dsl.element;

import java.util.Objects;

public class ColumnElement {
    private final String columnName;
    private final String typeHandlerName;

    public ColumnElement(String columnName, String typeHandlerName) {
        this.columnName = Objects.requireNonNull(columnName);
        this.typeHandlerName = typeHandlerName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTypeHandlerName() {
        return typeHandlerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnElement that = (ColumnElement) o;
        return Objects.equals(columnName, that.columnName) &&
                Objects.equals(typeHandlerName, that.typeHandlerName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(columnName, typeHandlerName);
    }

    @Override
    public String toString() {
        return "ColumnElement{" +
                "columnName='" + columnName + '\'' +
                ", typeHandlerName='" + typeHandlerName + '\'' +
                '}';
    }
}
