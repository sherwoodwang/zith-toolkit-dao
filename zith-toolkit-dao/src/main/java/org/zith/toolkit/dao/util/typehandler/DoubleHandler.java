package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class DoubleHandler implements DaoSqlTypeHandler<Double> {

    public DoubleHandler() {
    }

    @Override
    public Class<Double> type() {
        return Double.class;
    }

    @Override
    public String getSqlType() {
        return "DOUBLE";
    }

    @Override
    public int getJdbcType() {
        return Types.DOUBLE;
    }

    @Nullable
    @Override
    public Double load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        Double value = resultSet.getDouble(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Double load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        Double value = resultSet.getDouble(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Double load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        Double value = callableStatement.getDouble(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Double load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        Double value = callableStatement.getDouble(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable Double value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setDouble(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable Double value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setDouble(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable Double value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setDouble(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, Double value) {
        return value;
    }

    @Override
    public Double convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
