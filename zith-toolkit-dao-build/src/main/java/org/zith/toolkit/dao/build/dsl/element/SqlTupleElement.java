package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public class SqlTupleElement {
    private final String name;
    private final List<ColumnElement> fields;

    public SqlTupleElement(String name, List<ColumnElement> fields) {
        this.name = Objects.requireNonNull(name);
        this.fields = ImmutableList.copyOf(fields);
    }

    public String getName() {
        return name;
    }

    public List<ColumnElement> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTupleElement that = (SqlTupleElement) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, fields);
    }

    @Override
    public String toString() {
        return "SqlTupleElement{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}
