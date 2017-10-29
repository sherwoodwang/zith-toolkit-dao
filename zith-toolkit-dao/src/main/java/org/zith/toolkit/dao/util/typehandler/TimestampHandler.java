package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.*;

public class TimestampHandler implements DaoSqlTypeHandler<Timestamp> {
    private final String sqlType;
    private final int jdbcType;

    public TimestampHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Timestamp> type() {
        return Timestamp.class;
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
    public Timestamp load(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getTimestamp(columnIndex);
    }

    @Override
    public Timestamp load(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName);
    }

    @Override
    public Timestamp load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getTimestamp(columnIndex);
    }

    @Override
    public Timestamp load(CallableStatement callableStatement, String columnName) throws SQLException {
        return callableStatement.getTimestamp(columnName);
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Timestamp value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setTimestamp(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Timestamp value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setTimestamp(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Timestamp value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setTimestamp(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Timestamp value) {
        return value;
    }

    @Override
    public Timestamp convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Timestamp) {
            return (Timestamp) value; // TODO
        } else {
            throw new IllegalArgumentException();
        }
    }
}
