package org.zith.toolkit.dao.build.dsl.parser;

public class SqlTypeDictionaryItemElement {
    private final Type type;
    private final Object value;

    public SqlTypeDictionaryItemElement(SqlTypeHandlerElement sqlTypeHandlerElement) {
        this.type = Type.TYPE_HANDLER;
        this.value = sqlTypeHandlerElement;
    }

    public SqlTypeDictionaryItemElement(SqlTypeSelectorElement sqlTypeSelectorElement) {
        this.type = Type.TYPE_SELECTOR;
        this.value = sqlTypeSelectorElement;
    }

    public Type getType() {
        return type;
    }

    public SqlTypeHandlerElement getSqlTypeHandlerElement() {
        return (SqlTypeHandlerElement) value;
    }

    public SqlTypeSelectorElement getSqlTypeSelectorDeclaration() {
        return (SqlTypeSelectorElement) value;
    }

    public enum Type {
        TYPE_HANDLER,
        TYPE_SELECTOR,
    }
}
