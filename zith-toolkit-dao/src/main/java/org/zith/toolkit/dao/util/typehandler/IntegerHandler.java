package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerHandler implements DaoSqlTypeHandler<Integer> {
    private final String sqlType;
    private final int jdbcType;

    public IntegerHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Integer> type() {
        return Integer.class;
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
    public Integer load(ResultSet resultSet, int columnIndex) throws SQLException {
        Integer value = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Integer load(ResultSet resultSet, String columnName) throws SQLException {
        Integer value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Integer load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Integer value = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Integer load(CallableStatement callableStatement, String columnName) throws SQLException {
        Integer value = callableStatement.getInt(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setInt(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setInt(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Integer value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setInt(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Integer value) {
        return value;
    }

    @Override
    public Integer convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
