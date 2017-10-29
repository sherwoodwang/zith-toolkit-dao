package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleHandler implements DaoSqlTypeHandler<Double> {
    private final String sqlType;
    private final int jdbcType;

    public DoubleHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Double> type() {
        return Double.class;
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
    public Double load(ResultSet resultSet, int columnIndex) throws SQLException {
        Double value = resultSet.getDouble(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Double load(ResultSet resultSet, String columnName) throws SQLException {
        Double value = resultSet.getDouble(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Double load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Double value = callableStatement.getDouble(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Double load(CallableStatement callableStatement, String columnName) throws SQLException {
        Double value = callableStatement.getDouble(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Double value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setDouble(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Double value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setDouble(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Double value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setDouble(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Double value) {
        return value;
    }

    @Override
    public Double convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
