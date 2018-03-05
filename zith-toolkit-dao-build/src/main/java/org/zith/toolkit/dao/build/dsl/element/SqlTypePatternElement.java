package org.zith.toolkit.dao.build.dsl.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SqlTypePatternElement {

    private final List<String> sqlType;
    private final Map<String, Object> parameters;

    public SqlTypePatternElement(List<String> sqlType) {
        this.sqlType = ImmutableList.copyOf(sqlType);
        this.parameters = Collections.emptyMap();
    }

    public SqlTypePatternElement(List<String> sqlType, Map<String, Object> parameters) {
        this.sqlType = ImmutableList.copyOf(sqlType);
        this.parameters = ImmutableMap.copyOf(parameters);
    }

    public List<String> getSqlType() {
        return sqlType;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTypePatternElement that = (SqlTypePatternElement) o;
        return Objects.equals(sqlType, that.sqlType) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sqlType, parameters);
    }

    @Override
    public String toString() {
        return "SqlTypePatternElement{" +
                "sqlType=" + sqlType +
                ", parameters=" + parameters +
                '}';
    }
}
