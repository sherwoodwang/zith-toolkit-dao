package org.zith.toolkit.dao.util.spring;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zith.toolkit.dao.support.DaoSqlColumn;
import org.zith.toolkit.dao.support.DaoSqlTupleType;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DaoRecordCrudOperations<T> {
    private final JdbcTemplate jdbcTemplate;
    private final String table;
    private final DaoSqlTupleType<T> daoSqlTupleType;

    private final List<DaoSqlColumn<T, ?>> idColumns;
    private final List<DaoSqlColumn<T, ?>> dataColumns;
    private final DaoRecordRowMapper<T> rowMapper;

    private final String createSql;
    private final String readSql;
    private final String updateSql;
    private final String deleteSql;

    protected DaoRecordCrudOperations(Template<T> template) {
        this.jdbcTemplate = template.jdbcTemplate;
        this.table = template.table;
        this.daoSqlTupleType = template.daoSqlTupleType;

        this.idColumns = template.idColumns;
        this.dataColumns = template.dataColumns;
        this.rowMapper = template.rowMapper;

        createSql = template.sqlCreate;
        readSql = template.sqlRead;
        updateSql = template.sqlUpdate;
        deleteSql = template.sqlDelete;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public String getTable() {
        return table;
    }

    public DaoSqlTupleType<T> getDaoSqlTupleType() {
        return daoSqlTupleType;
    }

    public List<DaoSqlColumn<T, ?>> getIdColumns() {
        return idColumns;
    }

    public List<DaoSqlColumn<T, ?>> getDataColumns() {
        return dataColumns;
    }

    public DaoRecordRowMapper<T> getRowMapper() {
        return rowMapper;
    }

    public String getCreateSql() {
        return createSql;
    }

    public String getReadSql() {
        return readSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getDeleteSql() {
        return deleteSql;
    }

    public void create(T record) {
        getJdbcTemplate().update(
                getCreateSql(),
                Stream.concat(
                        getIdColumns().stream(),
                        getDataColumns().stream()
                )
                        .map(c -> getAsNativeValue(c, record))
                        .toArray(),
                Stream.concat(
                        getIdColumns().stream(),
                        getDataColumns().stream()
                )
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray()
        );
    }

    public T read(Object... idComponents) {
        checkIdComponents(idComponents);

        return getJdbcTemplate().queryForObject(
                getReadSql(),
                IntStream.range(0, getIdColumns().size()).boxed()
                        .map(i -> convertToNativeValue(getIdColumns().get(i).getTypeHandler(), idComponents[i]))
                        .toArray(),
                getIdColumns().stream()
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray(),
                getRowMapper()
        );
    }

    public void read(T record) {
        T refreshedRecord = getJdbcTemplate().queryForObject(
                getReadSql(),
                getIdColumns().stream()
                        .map(c -> getAsNativeValue(c, record))
                        .toArray(),
                getIdColumns().stream()
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray(),
                getRowMapper()
        );

        getDaoSqlTupleType().set(record, refreshedRecord);
    }

    public void update(T record) {
        getJdbcTemplate().update(
                getUpdateSql(),
                Stream.concat(
                        getDataColumns().stream(),
                        getIdColumns().stream()
                )
                        .map(c -> getAsNativeValue(c, record))
                        .toArray(),
                Stream.concat(
                        getDataColumns().stream(),
                        getIdColumns().stream()
                )
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray()
        );
    }

    public void delete(Object... idComponents) {
        checkIdComponents(idComponents);

        int c = getJdbcTemplate().update(
                getDeleteSql(),
                IntStream.range(0, getIdColumns().size()).boxed()
                        .map(i -> convertToNativeValue(getIdColumns().get(i).getTypeHandler(), idComponents[i]))
                        .toArray(),
                getIdColumns().stream()
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray()
        );

        if (c != 1) {
            throw new IncorrectResultSizeDataAccessException(1, c);
        }
    }

    public void delete(T record) {
        int count = getJdbcTemplate().update(
                getDeleteSql(),
                getIdColumns().stream()
                        .map(c -> getAsNativeValue(c, record))
                        .toArray(),
                getIdColumns().stream()
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray()
        );

        if (count != 1) {
            throw new IncorrectResultSizeDataAccessException(1, count);
        }
    }

    private void checkIdComponents(Object[] idComponents) {
        if (getIdColumns().size() != idComponents.length) {
            throw new IllegalArgumentException(String.format(
                    "Unmatched number of id components, expected: %d, actual: %d",
                    getIdColumns().size(),
                    idComponents.length
            ));
        }

        for (int i = 0; i < getIdColumns().size(); i++) {
            DaoSqlColumn<T, ?> column = getIdColumns().get(i);
            Object idComponent = idComponents[i];

            if (!column.getTypeHandler().type().isInstance(idComponent)) {
                throw new IllegalArgumentException(String.format(
                        "The value for column %s is of a wrong type, expected: %s, actual: %s",
                        column.getColumnName(),
                        column.getTypeHandler().type().getName(),
                        idComponent.getClass().getName()
                ));
            }
        }
    }

    protected static <T, U> Object getAsNativeValue(DaoSqlColumn<T, U> column, T record) {
        return column.getTypeHandler().convertToNativeValue(column.get(record));
    }

    protected static <T, U> void setAsNativeValue(DaoSqlColumn<T, U> column, T record, Object value) {
        column.set(record, column.getTypeHandler().convertFromNativeValue(value));
    }

    protected static <T> Object convertToNativeValue(DaoSqlTypeHandler<T> typeHandler, Object value) {
        return typeHandler.convertToNativeValue(typeHandler.type().cast(value));
    }

    @SafeVarargs
    public static <T> DaoRecordCrudOperations<T> create(
            JdbcTemplate jdbcTemplate,
            String table,
            DaoSqlTupleType<T> daoSqlTupleType,
            DaoSqlColumn<T, ?>... idColumns
    ) {
        Template<T> template = new Template<>();
        template.setUpValuesForDaoRecordCrudOperations(
                jdbcTemplate, table, daoSqlTupleType, idColumns
        );
        return new DaoRecordCrudOperations<>(template);
    }

    protected static class Template<T> {
        private JdbcTemplate jdbcTemplate;
        private String table;
        private DaoSqlTupleType<T> daoSqlTupleType;

        private List<DaoSqlColumn<T, ?>> idColumns;
        private List<DaoSqlColumn<T, ?>> dataColumns;
        private DaoRecordRowMapper<T> rowMapper;

        private String sqlCreate;
        private String sqlRead;
        private String sqlUpdate;
        private String sqlDelete;

        @SafeVarargs
        public final void setUpValuesForDaoRecordCrudOperations(
                JdbcTemplate jdbcTemplate,
                String table,
                DaoSqlTupleType<T> daoSqlTupleType,
                DaoSqlColumn<T, ?>... idColumns
        ) {
            this.jdbcTemplate = jdbcTemplate;
            this.table = table;
            this.daoSqlTupleType = daoSqlTupleType;

            this.idColumns = check(daoSqlTupleType, idColumns);
            this.dataColumns = dataColumns(daoSqlTupleType, this.idColumns);
            this.rowMapper = new DaoRecordRowMapper<>(daoSqlTupleType);

            sqlCreate = buildSqlCreate();
            sqlRead = buildSqlRead();
            sqlUpdate = buildSqlUpdate();
            sqlDelete = buildSqlDelete();
        }

        protected final JdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }

        protected final String getTable() {
            return table;
        }

        protected final DaoSqlTupleType<T> getDaoSqlTupleType() {
            return daoSqlTupleType;
        }

        protected final List<DaoSqlColumn<T, ?>> getIdColumns() {
            return idColumns;
        }

        protected final List<DaoSqlColumn<T, ?>> getDataColumns() {
            return dataColumns;
        }

        protected final DaoRecordRowMapper<T> getRowMapper() {
            return rowMapper;
        }

        protected final String getSqlCreate() {
            return sqlCreate;
        }

        protected final String getSqlRead() {
            return sqlRead;
        }

        protected final String getSqlUpdate() {
            return sqlUpdate;
        }

        protected final String getSqlDelete() {
            return sqlDelete;
        }

        private static <T> List<DaoSqlColumn<T, ?>> check(
                DaoSqlTupleType<T> daoSqlTupleType, DaoSqlColumn<T, ?>[] idColumns) {
            if (idColumns.length == 0) {
                throw new IllegalArgumentException("There must be at least one id column");
            }

            for (int i = 0; i < idColumns.length; i++) {
                for (int j = 0; j < idColumns.length; j++) {
                    if (i != j) {
                        if (idColumns[i] == idColumns[j]) {
                            throw new IllegalArgumentException(
                                    "Duplicated id column: " + idColumns[i].getColumnName());
                        }
                    }
                }
            }

            for (DaoSqlColumn<T, ?> idColumn : idColumns) {
                if (daoSqlTupleType.columns().all().stream().noneMatch(c -> c == idColumn)) {
                    throw new IllegalArgumentException(
                            "Column '" + idColumn.getColumnName() + "' is not defined in tuple");
                }
            }

            return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(idColumns)));
        }

        private static <T> List<DaoSqlColumn<T, ?>> dataColumns(
                DaoSqlTupleType<T> daoSqlTupleType, List<DaoSqlColumn<T, ?>> idColumns) {
            return Collections.unmodifiableList(daoSqlTupleType.columns().all().stream()
                    .filter(c -> idColumns.stream().noneMatch(idc -> c == idc))
                    .collect(Collectors.toList()));
        }

        protected String quoteSqlName(String name) {
            return name;
        }

        private String buildSqlCreate() {
            return "INSERT INTO " + table + " (" +
                    Stream.concat(
                            idColumns.stream(),
                            dataColumns.stream()
                    )
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .collect(Collectors.joining(", ")) +
                    ") VALUES (" +
                    Stream.concat(
                            idColumns.stream(),
                            dataColumns.stream()
                    )
                            .map(__ -> "?")
                            .collect(Collectors.joining(", ")) +
                    ")";
        }

        private String buildSqlRead() {
            return "SELECT " +
                    daoSqlTupleType.columns().all().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .collect(Collectors.joining(", ")) +
                    " FROM " + table + " WHERE " +
                    idColumns.stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(" AND "));
        }

        private String buildSqlUpdate() {
            return "UPDATE " + table + " SET " +
                    dataColumns.stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(", ")) +
                    " WHERE " +
                    idColumns.stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(" AND "));
        }

        private String buildSqlDelete() {
            return "DELETE FROM " + table + " WHERE " +
                    idColumns.stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(" AND "));
        }
    }
}
