package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringHandler implements DaoSqlTypeHandler<String> {
    private final String sqlType;
    private final int jdbcType;

    public StringHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public String getSqlType() {
        return sqlType;
    }

    @Override
    public int getJdbcType() {
        return jdbcType;
    }

    @Override
    public String load(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    @Override
    public String load(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getString(columnName);
    }

    @Override
    public String load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getString(columnIndex);
    }

    @Override
    public String load(CallableStatement callableStatement, String columnName) throws SQLException {
        return callableStatement.getString(columnName);
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, String value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setString(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, String value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setString(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, String value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setString(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(String value) {
        return value;
    }

    @Override
    public String convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }
}
