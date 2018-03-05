package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SqlTypeDictionaryItemElement {
    private final String handlerName;
    private final JavaReferenceElement typeReference;
    private final List<SqlTypePatternElement> patterns;

    public SqlTypeDictionaryItemElement(
            String handlerName,
            JavaReferenceElement typeReference,
            List<SqlTypePatternElement> patterns
    ) {
        this.handlerName = handlerName;
        this.typeReference = typeReference;
        this.patterns = Objects.requireNonNull(ImmutableList.copyOf(patterns));
    }

    public Optional<String> getHandlerName() {
        return Optional.ofNullable(handlerName);
    }

    public Optional<JavaReferenceElement> getTypeReference() {
        return Optional.ofNullable(typeReference);
    }

    public List<SqlTypePatternElement> getPatterns() {
        return patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTypeDictionaryItemElement that = (SqlTypeDictionaryItemElement) o;
        return Objects.equals(handlerName, that.handlerName) &&
                Objects.equals(typeReference, that.typeReference) &&
                Objects.equals(patterns, that.patterns);
    }

    @Override
    public int hashCode() {

        return Objects.hash(handlerName, typeReference, patterns);
    }

    @Override
    public String toString() {
        return "SqlTypeDictionaryItemElement{" +
                "handlerName='" + handlerName + '\'' +
                ", typeReference=" + typeReference +
                ", patterns=" + patterns +
                '}';
    }
}
