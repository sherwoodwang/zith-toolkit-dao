package org.zith.toolkit.dao.build.data;

import com.google.common.collect.ImmutableList;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

public class SqlTypeDetonator {
    private final List<String> value;

    public SqlTypeDetonator(List<String> value) {
        this.value = ImmutableList.copyOf(value);
    }

    public List<String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.stream().collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlTypeDetonator that = (SqlTypeDetonator) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
