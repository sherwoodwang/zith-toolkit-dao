package org.zith.toolkit.dao.build.dsl.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
}
