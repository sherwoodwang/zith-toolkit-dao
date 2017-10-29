package org.zith.toolkit.dao.build.dsl.parser;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class TupleElement {
    private final String name;
    private final List<ColumnElement> fields;

    TupleElement(String name, List<ColumnElement> fields) {
        this.name = name;
        this.fields = ImmutableList.copyOf(fields);
    }

    public String getName() {
        return name;
    }

    public List<ColumnElement> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "TupleElement{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}
