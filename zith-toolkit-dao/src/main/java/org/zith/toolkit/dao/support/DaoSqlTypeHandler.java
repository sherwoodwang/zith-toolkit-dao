package org.zith.toolkit.dao.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Objects;

public interface DaoSqlTypeHandler<T> {

    Class<T> type();

    String getSqlType();

    int getJdbcType();

    @Nullable
    T load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException;

    @Nullable
    T load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException;

    @Nullable
    T load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException;

    @Nullable
    T load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException;

    void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable T value) throws SQLException;

    void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable T value) throws SQLException;

    void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable T value) throws SQLException;

    Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, T value) throws SQLException;

    T convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) throws SQLException;

    @SuppressWarnings("unchecked")
    static <TR> DaoSqlTypeHandler<TR> cast(
            DaoSqlTypeHandler<?> th,
            Class<TR> tClass,
            String sqlType,
            int jdbcSqlType
    ) {
        if (th != null &&
                tClass.equals(th.type()) &&
                Objects.equals(th.getSqlType(), sqlType) &&
                Objects.equals(jdbcSqlType, th.getJdbcType())) {
            return (DaoSqlTypeHandler<TR>) th;
        }

        throw new ClassCastException();
    }

}
