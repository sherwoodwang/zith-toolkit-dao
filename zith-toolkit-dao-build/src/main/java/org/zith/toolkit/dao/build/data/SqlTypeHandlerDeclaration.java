package org.zith.toolkit.dao.build.data;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SqlTypeHandlerDeclaration {

    private final String name;
    private final List<TypeSelector> typeSelectors;
    private final String type;

    private final Map<SqlTypeDetonator, TypeSelector> typeSelectorsBySqlType;

    public SqlTypeHandlerDeclaration(String name, List<TypeSelector> typeSelectors, String type) {
        this.name = Objects.requireNonNull(name);
        this.typeSelectors = ImmutableList.copyOf(typeSelectors);
        this.type = Objects.requireNonNull(type);

        typeSelectorsBySqlType = Collections.unmodifiableMap(typeSelectors.stream()
                .collect(Collectors.toMap(TypeSelector::getRootSqlType, UnaryOperator.identity())));
    }

    public String getName() {
        return name;
    }

    public List<TypeSelector> getTypeSelectors() {
        return typeSelectors;
    }

    public String getType() {
        return type;
    }

    private int getJdbcType(SqlTypeDetonator sqlType) {
        TypeSelector typeSelector = typeSelectorsBySqlType.get(sqlType);

        if (typeSelector == null) {
            throw new NoSuchElementException();
        }

        return typeSelector.getJdbcType();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String type;
        private List<TypeSelector> typeSelectors;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder addTypeSelector(TypeSelector typeSelector) {
            if (this.typeSelectors == null) {
                this.typeSelectors = new LinkedList<>();
            }

            this.typeSelectors.add(typeSelector);

            return this;
        }

        public Builder addTypeSelectors(List<TypeSelector> typeSelectors) {
            if (this.typeSelectors == null) {
                this.typeSelectors = new LinkedList<>();
            }

            this.typeSelectors.addAll(typeSelectors);

            return this;
        }

        public SqlTypeHandlerDeclaration build() {
            if (this.typeSelectors == null) {
                this.typeSelectors = new LinkedList<>();
            }

            return new SqlTypeHandlerDeclaration(name, typeSelectors, type);
        }
    }

    public static class TypeSelector {
        private final SqlTypeDetonator rootSqlType;
        private final int jdbcType;
        private final int nice;

        public TypeSelector(SqlTypeDetonator rootSqlType, int jdbcType, int nice) {
            this.rootSqlType = Objects.requireNonNull(rootSqlType);
            this.jdbcType = jdbcType;
            this.nice = nice;
        }

        public SqlTypeDetonator getRootSqlType() {
            return rootSqlType;
        }

        public int getJdbcType() {
            return jdbcType;
        }

        public int getNice() {
            return nice;
        }
    }
}
