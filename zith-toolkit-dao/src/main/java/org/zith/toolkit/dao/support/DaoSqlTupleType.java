package org.zith.toolkit.dao.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DaoSqlTupleType<T> {
    T create();

    void set(T to, T from);

    Columns<T> columns();

    void load(T record, ResultSet resultSet) throws SQLException;

    interface Columns<T> {
        List<DaoSqlColumn<T, ?>> all();
    }
}
