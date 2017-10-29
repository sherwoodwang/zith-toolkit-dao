package org.zith.toolkit.dao.support;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public interface DaoSqlTypeHandler<T> {

    Class<T> type();

    String getSqlType();

    int getJdbcType();

    T load(ResultSet resultSet, int columnIndex) throws SQLException;

    T load(ResultSet resultSet, String columnName) throws SQLException;

    T load(CallableStatement callableStatement, int columnIndex) throws SQLException;

    T load(CallableStatement callableStatement, String columnName) throws SQLException;

    void store(PreparedStatement preparedStatement, int parameterIndex, T value) throws SQLException;

    void store(CallableStatement callableStatement, int parameterIndex, T value) throws SQLException;

    void store(CallableStatement callableStatement, String parameterName, T value) throws SQLException;

    Object convertToNativeValue(T value);

    T convertFromNativeValue(Object value);

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
