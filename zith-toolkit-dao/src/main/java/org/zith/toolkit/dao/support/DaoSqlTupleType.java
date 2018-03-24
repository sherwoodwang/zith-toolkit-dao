package org.zith.toolkit.dao.support;

import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DaoSqlTupleType<T> {
    T create();

    void set(T to, T from);

    Columns<T> columns();

    void load(@Nullable DaoSqlOperationContext context, T record, ResultSet resultSet) throws SQLException;

    interface Columns<T> {
        List<DaoSqlColumn<T, ?>> all();
    }
}
