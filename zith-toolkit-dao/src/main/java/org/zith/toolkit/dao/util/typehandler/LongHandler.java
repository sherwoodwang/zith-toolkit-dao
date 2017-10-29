package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongHandler implements DaoSqlTypeHandler<Long> {
    private final String sqlType;
    private final int jdbcType;

    public LongHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public String getSqlType() {
        return sqlType;
    }

    @Override
    public int getJdbcType() {
        return jdbcType;
    }

    @Override
    public Long load(ResultSet resultSet, int columnIndex) throws SQLException {
        long value = resultSet.getLong(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Long load(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Long load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        long value = callableStatement.getLong(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Long load(CallableStatement callableStatement, String columnName) throws SQLException {
        long value = callableStatement.getLong(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Long value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setLong(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Long value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setLong(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Long value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setLong(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Long value) {
        return value;
    }

    @Override
    public Long convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
