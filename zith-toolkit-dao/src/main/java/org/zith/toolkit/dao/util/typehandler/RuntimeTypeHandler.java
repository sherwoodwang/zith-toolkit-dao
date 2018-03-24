package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;
import java.util.Objects;

public class RuntimeTypeHandler<T> implements DaoSqlTypeHandler<T> {
    private final String sqlType;
    private final int jdbcType;
    private final Class<T> type;

    public RuntimeTypeHandler(String sqlType, int jdbcType, Class<T> type) {
        this.sqlType = Objects.requireNonNull(sqlType);
        this.jdbcType = jdbcType;
        this.type = type;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public String getSqlType() {
        return sqlType;
    }

    @Override
    public int getJdbcType() {
        return jdbcType;
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex, type);
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        return resultSet.getObject(columnName, type);
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getObject(columnIndex, type);
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        return callableStatement.getObject(columnName, type);
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable T value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setObject(parameterIndex, value, getJdbcType());
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable T value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setObject(parameterIndex, value, getJdbcType());
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable T value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setObject(parameterName, value, getJdbcType());
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, T value) {
        return value;
    }

    @Override
    public T convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        return type.cast(value);
    }
}
