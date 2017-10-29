package org.zith.toolkit.dao.build.dsl.parser;

public class SqlTypeSelectorElement {

    private final String typeHandlerName;
    private final SqlTypePatternElement sqlTypePatternElement;

    public SqlTypeSelectorElement(String typeHandlerName, SqlTypePatternElement sqlTypePatternElement) {
        this.typeHandlerName = typeHandlerName;
        this.sqlTypePatternElement = sqlTypePatternElement;
    }

    public String getTypeHandlerName() {
        return typeHandlerName;
    }

    public SqlTypePatternElement getSqlTypePatternElement() {
        return sqlTypePatternElement;
    }
}
