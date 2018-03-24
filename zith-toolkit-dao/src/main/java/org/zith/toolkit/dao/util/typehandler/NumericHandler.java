package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.math.BigDecimal;
import java.sql.*;

public class NumericHandler implements DaoSqlTypeHandler<BigDecimal> {

    public NumericHandler() {
    }

    @Override
    public Class<BigDecimal> type() {
        return BigDecimal.class;
    }

    @Override
    public String getSqlType() {
        return "NUMERIC";
    }

    @Override
    public int getJdbcType() {
        return Types.NUMERIC;
    }

    @Nullable
    @Override
    public BigDecimal load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getBigDecimal(columnIndex);
    }

    @Nullable
    @Override
    public BigDecimal load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        return resultSet.getBigDecimal(columnName);
    }

    @Nullable
    @Override
    public BigDecimal load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getBigDecimal(columnIndex);
    }

    @Nullable
    @Override
    public BigDecimal load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        return callableStatement.getBigDecimal(columnName);
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable BigDecimal value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setBigDecimal(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable BigDecimal value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setBigDecimal(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable BigDecimal value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setBigDecimal(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, BigDecimal value) {
        return value;
    }

    @Override
    public BigDecimal convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if ((value instanceof Long) ||
                (value instanceof Integer) ||
                (value instanceof Short) ||
                (value instanceof Byte)) {
            return BigDecimal.valueOf(((Number) value).longValue());
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
