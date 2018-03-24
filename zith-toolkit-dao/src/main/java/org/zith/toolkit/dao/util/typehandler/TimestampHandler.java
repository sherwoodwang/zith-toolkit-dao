package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class TimestampHandler implements DaoSqlTypeHandler<Timestamp> {
    public TimestampHandler() {
    }

    @Override
    public Class<Timestamp> type() {
        return Timestamp.class;
    }

    @Override
    public String getSqlType() {
        return "TIMESTAMP";
    }

    @Override
    public int getJdbcType() {
        return Types.TIMESTAMP;
    }

    @Nullable
    @Override
    public Timestamp load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getTimestamp(columnIndex);
    }

    @Nullable
    @Override
    public Timestamp load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName);
    }

    @Nullable
    @Override
    public Timestamp load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getTimestamp(columnIndex);
    }

    @Nullable
    @Override
    public Timestamp load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        return callableStatement.getTimestamp(columnName);
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable Timestamp value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setTimestamp(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable Timestamp value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setTimestamp(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable Timestamp value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setTimestamp(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, Timestamp value) {
        return value;
    }

    @Override
    public Timestamp convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Timestamp) {
            return (Timestamp) value; // TODO
        } else {
            throw new IllegalArgumentException();
        }
    }
}
