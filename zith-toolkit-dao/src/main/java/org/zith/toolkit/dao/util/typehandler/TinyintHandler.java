package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class TinyintHandler implements DaoSqlTypeHandler<Byte> {

    public TinyintHandler() {
    }

    @Override
    public Class<Byte> type() {
        return Byte.class;
    }

    @Override
    public String getSqlType() {
        return "TINYINT";
    }

    @Override
    public int getJdbcType() {
        return Types.TINYINT;
    }

    @Nullable
    @Override
    public Byte load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        Byte value = resultSet.getByte(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Byte load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        Byte value = resultSet.getByte(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Byte load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        Byte value = callableStatement.getByte(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Byte load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        Byte value = callableStatement.getByte(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable Byte value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setByte(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable Byte value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setByte(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable Byte value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setByte(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, Byte value) {
        return value;
    }

    @Override
    public Byte convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
