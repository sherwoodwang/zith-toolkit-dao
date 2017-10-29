package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanHandler implements DaoSqlTypeHandler<Boolean> {
    private final String sqlType;
    private final int jdbcType;

    public BooleanHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
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
    public Boolean load(ResultSet resultSet, int columnIndex) throws SQLException {
        Boolean value = resultSet.getBoolean(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Boolean load(ResultSet resultSet, String columnName) throws SQLException {
        Boolean value = resultSet.getBoolean(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Boolean load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Boolean value = callableStatement.getBoolean(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Boolean load(CallableStatement callableStatement, String columnName) throws SQLException {
        Boolean value = callableStatement.getBoolean(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Boolean value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setBoolean(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Boolean value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setBoolean(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Boolean value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setBoolean(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Boolean value) {
        return value;
    }

    @Override
    public Boolean convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
