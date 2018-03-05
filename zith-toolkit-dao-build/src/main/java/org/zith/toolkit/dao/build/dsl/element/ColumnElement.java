package org.zith.toolkit.dao.build.dsl.element;

import java.util.Objects;

public class ColumnElement {
    private final String columnName;
    private final SqlTypeElement sqlTypeElement;

    public ColumnElement(String columnName, SqlTypeElement sqlTypeElement) {
        this.columnName = Objects.requireNonNull(columnName);
        this.sqlTypeElement = Objects.requireNonNull(sqlTypeElement);
    }

    public String getColumnName() {
        return columnName;
    }

    public SqlTypeElement getSqlTypeElement() {
        return sqlTypeElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnElement that = (ColumnElement) o;
        return Objects.equals(columnName, that.columnName) &&
                Objects.equals(sqlTypeElement, that.sqlTypeElement);
    }

    @Override
    public int hashCode() {

        return Objects.hash(columnName, sqlTypeElement);
    }

    @Override
    public String toString() {
        return "ColumnElement{" +
                "columnName='" + columnName + '\'' +
                ", sqlTypeElement=" + sqlTypeElement +
                '}';
    }
}
