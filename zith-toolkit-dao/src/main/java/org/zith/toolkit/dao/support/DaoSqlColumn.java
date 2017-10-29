package org.zith.toolkit.dao.support;

public interface DaoSqlColumn<R, T> {
    String getColumnName();

    String getFieldName();

    DaoSqlTypeHandler<T> getTypeHandler();

    void set(R record, T value);

    T get(R record);
}
