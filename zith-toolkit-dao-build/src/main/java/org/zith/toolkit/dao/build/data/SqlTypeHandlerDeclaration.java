package org.zith.toolkit.dao.build.data;

import java.util.Objects;

public class SqlTypeHandlerDeclaration {

    private final String name;
    private final String type;

    public SqlTypeHandlerDeclaration(String name, String type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
