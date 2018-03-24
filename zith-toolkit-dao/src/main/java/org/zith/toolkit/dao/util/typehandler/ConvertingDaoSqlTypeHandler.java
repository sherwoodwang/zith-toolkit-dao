package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public abstract class ConvertingDaoSqlTypeHandler<T, U> implements DaoSqlTypeHandler<T> {
    private final DaoSqlTypeHandler<U> base;

    protected ConvertingDaoSqlTypeHandler(DaoSqlTypeHandler<U> base) {
        this.base = base;
    }

    @Override
    public String getSqlType() {
        return base.getSqlType();
    }

    @Override
    public int getJdbcType() {
        return base.getJdbcType();
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        return unpack(base.load(context, resultSet, columnIndex));
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        return unpack(base.load(context, resultSet, columnName));
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        return unpack(base.load(context, callableStatement, columnIndex));
    }

    @Nullable
    @Override
    public T load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        return unpack(base.load(context, callableStatement, columnName));
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable T value) throws SQLException {
        base.store(context, preparedStatement, parameterIndex, pack(value));
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable T value) throws SQLException {
        base.store(context, callableStatement, parameterIndex, pack(value));
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable T value) throws SQLException {
        base.store(context, callableStatement, parameterName, pack(value));
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, T value) throws SQLException {
        return base.convertToNativeValue(context, connection, pack(value));
    }

    @Override
    public T convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) throws SQLException {
        return unpack(base.convertFromNativeValue(context, value));
    }

    protected abstract T unpack(U value);

    protected abstract U pack(T value);
}
