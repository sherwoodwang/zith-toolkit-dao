package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteHandler implements DaoSqlTypeHandler<Byte> {
    private final String sqlType;
    private final int jdbcType;

    public ByteHandler(String sqlType, int jdbcType) {
        this.sqlType = sqlType;
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<Byte> type() {
        return Byte.class;
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
    public Byte load(ResultSet resultSet, int columnIndex) throws SQLException {
        Byte value = resultSet.getByte(columnIndex);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Byte load(ResultSet resultSet, String columnName) throws SQLException {
        Byte value = resultSet.getByte(columnName);
        return resultSet.wasNull() ? null : value;
    }

    @Override
    public Byte load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Byte value = callableStatement.getByte(columnIndex);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public Byte load(CallableStatement callableStatement, String columnName) throws SQLException {
        Byte value = callableStatement.getByte(columnName);
        return callableStatement.wasNull() ? null : value;
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, Byte value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            preparedStatement.setByte(parameterIndex, value);
        }
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, Byte value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterIndex, getJdbcType(), getSqlType());
        } else {
            callableStatement.setByte(parameterIndex, value);
        }

    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, Byte value) throws SQLException {
        if (value == null) {
            callableStatement.setNull(parameterName, getJdbcType(), getSqlType());
        } else {
            callableStatement.setByte(parameterName, value);
        }
    }

    @Override
    public Object convertToNativeValue(Byte value) {
        return value;
    }

    @Override
    public Byte convertFromNativeValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
