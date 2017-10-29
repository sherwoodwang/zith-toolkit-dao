package org.zith.toolkit.dao.build.dsl.parser;

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
}
