package org.zith.toolkit.dao.build.dsl.element;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class SqlTypeDictionaryItemElement {
    private final String handlerName;
    private final JavaReferenceElement type;

    public SqlTypeDictionaryItemElement(
            @Nullable String handlerName,
            JavaReferenceElement type
    ) {
        this.handlerName = handlerName;
        this.type = Objects.requireNonNull(type);
    }

    public Optional<String> getHandlerName() {
        return Optional.ofNullable(handlerName);
    }

    public JavaReferenceElement getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTypeDictionaryItemElement that = (SqlTypeDictionaryItemElement) o;
        return Objects.equals(handlerName, that.handlerName) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(handlerName, type);
    }

    @Override
    public String toString() {
        return "SqlTypeDictionaryItemElement{" +
                "handlerName='" + handlerName + '\'' +
                ", type=" + type +
                '}';
    }
}
