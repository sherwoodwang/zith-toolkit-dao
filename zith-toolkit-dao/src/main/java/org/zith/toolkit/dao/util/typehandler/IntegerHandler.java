package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class IntegerHandler implements DaoSqlTypeHandler<Integer> {
    public IntegerHandler() {
    }

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    public String getSqlType() {
        return "INTEGER";
    }

    @Override
    public int getJdbcType() {
        return Types.INTEGER;
    }

    @Nullable
    @Override
    public Integer load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        Integer value = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Integer load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        Integer value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Integer load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        Integer value = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Integer load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        Integer value = callableStatement.getInt(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable Integer value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setInt(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable Integer value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setInt(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable Integer value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setInt(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, Integer value) {
        return value;
    }

    @Override
    public Integer convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
