package org.zith.toolkit.dao.util.spring;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.SqlValue;
import org.zith.toolkit.dao.support.DaoSqlColumn;
import org.zith.toolkit.dao.support.DaoSqlTupleType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DaoRecordCrudOperations<T> {
    private final JdbcTemplate jdbcTemplate;
    private final String table;
    private final DaoSqlTupleType<T> daoSqlTupleType;

    private final List<DaoSqlColumn<T, ?>> idColumns;
    private final List<DaoSqlColumn<T, ?>> dataColumns;
    private final DaoRecordRowMapper<T> rowMapper;

    private final String sqlCreate;
    private final String sqlRead;
    private final String sqlUpdate;
    private final String sqlDelete;

    protected DaoRecordCrudOperations(Template<T> template) {
        this.jdbcTemplate = template.getJdbcTemplate();
        this.table = template.getTable();
        this.daoSqlTupleType = template.getDaoSqlTupleType();

        this.idColumns = template.getIdColumns();
        this.dataColumns = template.getDataColumns();
        this.rowMapper = template.getRowMapper();

        sqlCreate = template.getSqlCreate();
        sqlRead = template.getSqlRead();
        sqlUpdate = template.getSqlUpdate();
        sqlDelete = template.getSqlDelete();
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

    public void create(T record) {
        getJdbcTemplate().update(
                sqlCreate,
                Stream.concat(
                        getIdColumns().stream(),
                        getDataColumns().stream()
                )
                        .map(c -> extract(c, record))
                        .toArray()
        );
    }

    public T read(Object... idComponents) {
        checkIdComponents(idComponents);

        return getJdbcTemplate().queryForObject(
                sqlRead,
                IntStream.range(0, getIdColumns().size()).boxed()
                        .map(i -> TypeHandlerBasedSqlValue.from(getIdColumns().get(i).getTypeHandler(), idComponents[i]))
                        .toArray(),
                getRowMapper()
        );
    }

    public void read(T record) {
        T refreshedRecord = getJdbcTemplate().queryForObject(
                sqlRead,
                getIdColumns().stream()
                        .map(c -> extract(c, record))
                        .toArray(),
                getRowMapper()
        );

        getDaoSqlTupleType().set(record, refreshedRecord);
    }

    public void update(T record) {
        int count = getJdbcTemplate().update(
                sqlUpdate,
                Stream.concat(
                        getDataColumns().stream(),
                        getIdColumns().stream()
                )
                        .map(c -> extract(c, record))
                        .toArray()
        );

        if (count != 1) {
            throw new IncorrectResultSizeDataAccessException(1, count);
        }
    }

    public void delete(Object... idComponents) {
        checkIdComponents(idComponents);

        int count = getJdbcTemplate().update(
                sqlDelete,
                IntStream.range(0, getIdColumns().size()).boxed()
                        .map(i -> TypeHandlerBasedSqlValue.from(getIdColumns().get(i).getTypeHandler(), idComponents[i]))
                        .toArray()
        );

        if (count != 1) {
            throw new IncorrectResultSizeDataAccessException(1, count);
        }
    }

    public void delete(T record) {
        int count = getJdbcTemplate().update(
                sqlDelete,
                getIdColumns().stream()
                        .map(c -> extract(c, record))
                        .toArray()
        );

        if (count != 1) {
            throw new IncorrectResultSizeDataAccessException(1, count);
        }
    }

    protected void checkIdComponents(Object[] idComponents) {
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

    protected static <T, U> SqlValue extract(DaoSqlColumn<T, U> column, T record) {
        U value = column.get(record);
        return TypeHandlerBasedSqlValue.from(column.getTypeHandler(), value);
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

            return List.of(idColumns);
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
            return "INSERT INTO " + getTable() + " (" +
                    Stream.concat(
                            getIdColumns().stream(),
                            getDataColumns().stream()
                    )
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .collect(Collectors.joining(", ")) +
                    ") VALUES (" +
                    Stream.concat(
                            getIdColumns().stream(),
                            getDataColumns().stream()
                    )
                            .map(__ -> "?")
                            .collect(Collectors.joining(", ")) +
                    ")";
        }

        private String buildSqlRead() {
            return "SELECT " +
                    getDaoSqlTupleType().columns().all().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .collect(Collectors.joining(", ")) +
                    " FROM " + getTable() + " WHERE " +
                    getIdColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(" AND "));
        }

        private String buildSqlUpdate() {
            return "UPDATE " + getTable() + " SET " +
                    getDataColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(", ")) +
                    " WHERE " +
                    getIdColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(" AND "));
        }

        private String buildSqlDelete() {
            return "DELETE FROM " + getTable() + " WHERE " +
                    getIdColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .map(this::quoteSqlName)
                            .map(c -> c + "=?")
                            .collect(Collectors.joining(" AND "));
        }
    }

}
