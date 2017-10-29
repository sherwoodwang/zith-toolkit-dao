package org.zith.toolkit.dao.util.typehandler;

import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    @Override
    public T load(ResultSet resultSet, int columnIndex) throws SQLException {
        return load(base.load(resultSet, columnIndex));
    }

    @Override
    public T load(ResultSet resultSet, String columnName) throws SQLException {
        return load(base.load(resultSet, columnName));
    }

    @Override
    public T load(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return load(base.load(callableStatement, columnIndex));
    }

    @Override
    public T load(CallableStatement callableStatement, String columnName) throws SQLException {
        return load(base.load(callableStatement, columnName));
    }

    @Override
    public void store(PreparedStatement preparedStatement, int parameterIndex, T value) throws SQLException {
        base.store(preparedStatement, parameterIndex, store(value));
    }

    @Override
    public void store(CallableStatement callableStatement, int parameterIndex, T value) throws SQLException {
        base.store(callableStatement, parameterIndex, store(value));
    }

    @Override
    public void store(CallableStatement callableStatement, String parameterName, T value) throws SQLException {
        base.store(callableStatement, parameterName, store(value));
    }

    @Override
    public Object convertToNativeValue(T value) {
        return base.convertToNativeValue(store(value));
    }

    @Override
    public T convertFromNativeValue(Object value) {
        return load(base.convertFromNativeValue(value));
    }

    protected abstract T load(U value);

    protected abstract U store(T value);
}
