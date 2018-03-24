package org.zith.toolkit.dao.util.typehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zith.toolkit.dao.support.DaoSqlOperationContext;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class VarcharHandler implements DaoSqlTypeHandler<String> {
    public VarcharHandler() {
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public String getSqlType() {
        return "VARCHAR";
    }

    @Override
    public int getJdbcType() {
        return Types.VARCHAR;
    }

    @Nullable
    @Override
    public String load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    @Nullable
    @Override
    public String load(@Nullable DaoSqlOperationContext context, @NotNull ResultSet resultSet, @NotNull String columnName) throws SQLException {
        return resultSet.getString(columnName);
    }

    @Nullable
    @Override
    public String load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getString(columnIndex);
    }

    @Nullable
    @Override
    public String load(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String columnName) throws SQLException {
        return callableStatement.getString(columnName);
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull PreparedStatement preparedStatement, int parameterIndex, @Nullable String value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setString(parameterIndex, value);
        }
    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, int parameterIndex, @Nullable String value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setString(parameterIndex, value);
        }

    }

    @Override
    public void store(@Nullable DaoSqlOperationContext context, @NotNull CallableStatement callableStatement, @NotNull String parameterName, @Nullable String value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setString(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(@Nullable DaoSqlOperationContext context, Connection connection, String value) {
        return value;
    }

    @Override
    public String convertFromNativeValue(@Nullable DaoSqlOperationContext context, Object value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }
}
