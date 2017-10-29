package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RuntimeTypeHandler<T> implements DaoSqlTypeHandler<T> {
    private final String sqlType;
    private final int jdbcType;
    private final Class<T> type;

    public RuntimeTypeHandler(String sqlType, int jdbcType, Class<T> type) {
        this.sqlType = sqlType;
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

    @Override
    public T load(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex, type);
    }

    @Override
    public T load(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getObject(columnName, type);
    }

    @Override
    public T load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getObject(columnIndex, type);
    }

    @Override
    public T load(CallableStatement callableStatement, String columnName) throws SQLException {
        return callableStatement.getObject(columnName, type);
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, T value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setObject(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, T value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setObject(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, T value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setObject(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(T value) {
        return value;
    }

    @Override
    public T convertFromNativeValue(Object value) {
        return type.cast(value);
    }
}
