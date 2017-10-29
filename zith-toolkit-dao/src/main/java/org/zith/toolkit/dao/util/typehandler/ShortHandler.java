package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortHandler implements DaoSqlTypeHandler<Short> {
    private final String sqlType;
    private final int jdbcType;

    public ShortHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Short> type() {
        return Short.class;
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
    public Short load(ResultSet resultSet, int columnIndex) throws SQLException {
        Short value = resultSet.getShort(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Short load(ResultSet resultSet, String columnName) throws SQLException {
        Short value = resultSet.getShort(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Short load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Short value = callableStatement.getShort(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Short load(CallableStatement callableStatement, String columnName) throws SQLException {
        Short value = callableStatement.getShort(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Short value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setShort(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Short value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setShort(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Short value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setShort(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Short value) {
        return value;
    }

    @Override
    public Short convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
