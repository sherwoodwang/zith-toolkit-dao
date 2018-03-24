package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class SmallintHandler implements DaoSqlTypeHandler<Short> {
    public SmallintHandler() {
    }

    @Override
    public Class<Short> type() {
        return Short.class;
    }

    @Override
    public String getSqlType() {
        return "SMALLINT";
    }

    @Override
    public int getJdbcType() {
        return Types.SMALLINT;
    }

    @Nullable
    @Override
    public Short load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        Short value = resultSet.getShort(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Short load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        Short value = resultSet.getShort(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Short load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        Short value = callableStatement.getShort(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Nullable
    @Override
    public Short load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        Short value = callableStatement.getShort(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable Short value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setShort(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable Short value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setShort(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable Short value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setShort(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, Short value) {
        return value;
    }

    @Override
    public Short convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
