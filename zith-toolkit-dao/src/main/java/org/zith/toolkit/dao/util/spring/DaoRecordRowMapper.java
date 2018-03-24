package org.zith.toolkit.dao.util.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.zith.toolkit.dao.support.DaoSqlTupleType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class DaoRecordRowMapper<T> implements RowMapper<T> {

    private final DaoSqlTupleType<T> daoSqlTupleType;

    public DaoRecordRowMapper(DaoSqlTupleType<T> daoSqlTupleType) {
        this.daoSqlTupleType = daoSqlTupleType;
    }

    @Override
    @NotNull
    public T mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        T record = Objects.requireNonNull(daoSqlTupleType.create());
        daoSqlTupleType.load(null, record, rs);
        return record;
    }
}
