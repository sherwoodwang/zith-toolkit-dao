package org.zith.toolkit.dao.util.spring;

import org.springframework.jdbc.core.RowMapper;
import org.zith.toolkit.dao.support.DaoSqlTupleType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoRecordRowMapper<T> implements RowMapper<T> {

    private final DaoSqlTupleType<T> daoSqlTupleType;

    public DaoRecordRowMapper(DaoSqlTupleType<T> daoSqlTupleType) {
        this.daoSqlTupleType = daoSqlTupleType;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T record = daoSqlTupleType.create();
        daoSqlTupleType.load(record, rs);
        return record;
    }
}
