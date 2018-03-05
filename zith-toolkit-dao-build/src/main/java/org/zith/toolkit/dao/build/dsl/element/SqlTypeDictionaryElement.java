package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public class SqlTypeDictionaryElement {

    private final String name;
    private final List<SqlTypeDictionaryItemElement> items;

    public SqlTypeDictionaryElement(
            String name,
            List<SqlTypeDictionaryItemElement> items
    ) {
        this.name = Objects.requireNonNull(name);
        this.items = ImmutableList.copyOf(items);
    }

    public String getName() {
        return name;
    }

    public List<SqlTypeDictionaryItemElement> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTypeDictionaryElement that = (SqlTypeDictionaryElement) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, items);
    }

    @Override
    public String toString() {
        return "SqlTypeDictionaryElement{" +
                "name='" + name + '\'' +
                ", items=" + items +
                '}';
    }
}
