package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class BigintHandler implements DaoSqlTypeHandler<Long> {
    public BigintHandler() {
    }

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public String getSqlType() {
        return "BIGINT";
    }

    @Override
    public int getJdbcType() {
        return Types.BIGINT;
    }

    @Nullable
    @Override
    public Long load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        long value = resultSet.getLong(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Long load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Long load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        long value = callableStatement.getLong(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Long load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        long value = callableStatement.getLong(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable Long value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setLong(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable Long value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setLong(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable Long value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setLong(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, Long value) {
        return value;
    }

    @Override
    public Long convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
