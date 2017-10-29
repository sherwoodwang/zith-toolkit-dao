package org.zith.toolkit.dao.build.dsl.parser;

public class ColumnElement {
    private final String columnName;
    private final SqlTypeElement sqlTypeElement;

    ColumnElement(String columnName, SqlTypeElement sqlTypeElement) {
        this.columnName = columnName;
        this.sqlTypeElement = sqlTypeElement;
    }

    public String getColumnName() {
        return columnName;
    }

    public SqlTypeElement getSqlTypeElement() {
        return sqlTypeElement;
    }

    @Override
    public String toString() {
        return "ColumnElement{" +
                "columnName='" + columnName + '\'' +
                ", sqlTypeElement=" + sqlTypeElement +
                '}';
    }
}
