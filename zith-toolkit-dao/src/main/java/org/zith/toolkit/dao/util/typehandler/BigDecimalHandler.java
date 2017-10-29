package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalHandler implements DaoSqlTypeHandler<BigDecimal> {
    private final String sqlType;
    private final int jdbcType;

    public BigDecimalHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<BigDecimal> type() {
        return BigDecimal.class;
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
    public BigDecimal load(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getBigDecimal(columnIndex);
    }

    @Override
    public BigDecimal load(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getBigDecimal(columnName);
    }

    @Override
    public BigDecimal load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getBigDecimal(columnIndex);
    }

    @Override
    public BigDecimal load(CallableStatement callableStatement, String columnName) throws SQLException {
        return callableStatement.getBigDecimal(columnName);
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, BigDecimal value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setBigDecimal(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, BigDecimal value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setBigDecimal(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, BigDecimal value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setBigDecimal(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(BigDecimal value) {
        return value;
    }

    @Override
    public BigDecimal convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if ((value instanceof Long) ||
                (value instanceof Integer) ||
                (value instanceof Short) ||
                (value instanceof Byte)) {
            return BigDecimal.valueOf(((Number) value).longValue());
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
