package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatHandler implements DaoSqlTypeHandler<Float> {
    private final String sqlType;
    private final int jdbcType;

    public FloatHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Float> type() {
        return Float.class;
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
    public Float load(ResultSet resultSet, int columnIndex) throws SQLException {
        Float value = resultSet.getFloat(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Float load(ResultSet resultSet, String columnName) throws SQLException {
        Float value = resultSet.getFloat(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Float load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Float value = callableStatement.getFloat(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Float load(CallableStatement callableStatement, String columnName) throws SQLException {
        Float value = callableStatement.getFloat(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Float value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setFloat(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Float value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setFloat(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Float value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setFloat(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Float value) {
        return value;
    }

    @Override
    public Float convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
