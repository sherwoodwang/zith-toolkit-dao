package org.zith.toolkit.dao.util.spring;

import org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.zith.toolkit.dao.support.DaoSqlColumn;
import org.zith.toolkit.dao.support.DaoSqlTupleType;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;
import org.zith.toolkit.dao.util.sql.PgSqls;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DaoRecordPgCrudOperations<T> extends DaoRecordCrudOperations<T> {

    private final String sqlCreateWithGeneratedId;
    private final String sqlCreateIfNotExists;
    private final Map<ReadVariant, String> sqlReadVariants;

    private DaoRecordPgCrudOperations(
            Template<T> template
    ) {
        super(template);

        sqlCreateWithGeneratedId = template.getSqlCreateWithGeneratedId();
        sqlCreateIfNotExists = template.getSqlCreateIfNotExists();
        sqlReadVariants = template.getSqlReadVariants();
    }

    public void createWithGeneratedId(T record) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        ArgumentTypePreparedStatementSetter pss = new ArgumentTypePreparedStatementSetter(
                getDataColumns().stream()
                        .map(c -> getAsNativeValue(c, record))
                        .toArray(),
                getDataColumns().stream()
                        .map(DaoSqlColumn::getTypeHandler)
                        .mapToInt(DaoSqlTypeHandler::getJdbcType)
                        .toArray()
        );

        String[] idColumnNames = getIdColumns().stream()
                .map(DaoSqlColumn::getColumnName)
                .toArray(String[]::new);

        getJdbcTemplate().update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(sqlCreateWithGeneratedId, idColumnNames);
                    pss.setValues(ps);
                    return ps;
                },
                keyHolder
        );

        Map<String, Object> keys = keyHolder.getKeys();

        if (keys == null) {
            throw new IllegalStateException();
        }

        for (DaoSqlColumn<T, ?> column : getIdColumns()) {
            setAsNativeValue(column, record, keys.get(column.getColumnName()));
        }
    }

    public boolean createIfNotExists(T record) {
        int count = getJdbcTemplate().update(
                sqlCreateIfNotExists,
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

        return count > 0;
    }

    public T read(LockLevel lockLevel, WaitingMode waitingMode, Object... idComponents) {
        checkIdComponents(idComponents);
        String sql = sqlReadVariants.get(new ReadVariant(lockLevel, waitingMode));

        return getJdbcTemplate().queryForObject(
                sql,
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

    public void read(LockLevel lockLevel, WaitingMode waitingMode, T record) {
        String sql = sqlReadVariants.get(new ReadVariant(lockLevel, waitingMode));

        T refreshedRecord = getJdbcTemplate().queryForObject(
                sql,
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

    public T readForUpdate(Object... idComponents) {
        return read(LockLevel.UPDATE, WaitingMode.WAIT, idComponents);
    }

    public T readForNoIdUpdate(Object... idComponents) {
        return read(LockLevel.NO_ID_UPDATE, WaitingMode.WAIT, idComponents);
    }

    public T readForShare(Object... idComponents) {
        return read(LockLevel.SHARE, WaitingMode.WAIT, idComponents);
    }

    public T readForIdShare(Object... idComponents) {
        return read(LockLevel.ID_SHARE, WaitingMode.WAIT, idComponents);
    }

    public T readNoWait(Object... idComponents) {
        return read(LockLevel.NONE, WaitingMode.NO_WAIT, idComponents);
    }

    public T readForUpdateNoWait(Object... idComponents) {
        return read(LockLevel.UPDATE, WaitingMode.NO_WAIT, idComponents);
    }

    public T readForNoIdUpdateNoWait(Object... idComponents) {
        return read(LockLevel.NO_ID_UPDATE, WaitingMode.NO_WAIT, idComponents);
    }

    public T readForShareNoWait(Object... idComponents) {
        return read(LockLevel.SHARE, WaitingMode.NO_WAIT, idComponents);
    }

    public T readForIdShareNoWait(Object... idComponents) {
        return read(LockLevel.ID_SHARE, WaitingMode.NO_WAIT, idComponents);
    }

    public T readSkipLocked(Object... idComponents) {
        return read(LockLevel.NONE, WaitingMode.SKIP_LOCKED, idComponents);
    }

    public T readForUpdateSkipLocked(Object... idComponents) {
        return read(LockLevel.UPDATE, WaitingMode.SKIP_LOCKED, idComponents);
    }

    public T readForNoIdUpdateSkipLocked(Object... idComponents) {
        return read(LockLevel.NO_ID_UPDATE, WaitingMode.SKIP_LOCKED, idComponents);
    }

    public T readForShareSkipLocked(Object... idComponents) {
        return read(LockLevel.SHARE, WaitingMode.SKIP_LOCKED, idComponents);
    }

    public T readForIdShareSkipLocked(Object... idComponents) {
        return read(LockLevel.ID_SHARE, WaitingMode.SKIP_LOCKED, idComponents);
    }

    public void readForUpdate(T record) {
        read(LockLevel.UPDATE, WaitingMode.WAIT, record);
    }

    public void readForNoIdUpdate(T record) {
        read(LockLevel.NO_ID_UPDATE, WaitingMode.WAIT, record);
    }

    public void readForShare(T record) {
        read(LockLevel.SHARE, WaitingMode.WAIT, record);
    }

    public void readForIdShare(T record) {
        read(LockLevel.ID_SHARE, WaitingMode.WAIT, record);
    }

    public void readNoWait(T record) {
        read(LockLevel.NONE, WaitingMode.NO_WAIT, record);
    }

    public void readForUpdateNoWait(T record) {
        read(LockLevel.UPDATE, WaitingMode.NO_WAIT, record);
    }

    public void readForNoIdUpdateNoWait(T record) {
        read(LockLevel.NO_ID_UPDATE, WaitingMode.NO_WAIT, record);
    }

    public void readForShareNoWait(T record) {
        read(LockLevel.SHARE, WaitingMode.NO_WAIT, record);
    }

    public void readForIdShareNoWait(T record) {
        read(LockLevel.ID_SHARE, WaitingMode.NO_WAIT, record);
    }

    public void readSkipLocked(T record) {
        read(LockLevel.NONE, WaitingMode.SKIP_LOCKED, record);
    }

    public void readForUpdateSkipLocked(T record) {
        read(LockLevel.UPDATE, WaitingMode.SKIP_LOCKED, record);
    }

    public void readForNoIdUpdateSkipLocked(T record) {
        read(LockLevel.NO_ID_UPDATE, WaitingMode.SKIP_LOCKED, record);
    }

    public void readForShareSkipLocked(T record) {
        read(LockLevel.SHARE, WaitingMode.SKIP_LOCKED, record);
    }

    public void readForIdShareSkipLocked(T record) {
        read(LockLevel.ID_SHARE, WaitingMode.SKIP_LOCKED, record);
    }

    public enum LockLevel {
        NONE,
        UPDATE,
        NO_ID_UPDATE,
        SHARE,
        ID_SHARE,
    }

    public enum WaitingMode {
        WAIT,
        NO_WAIT,
        SKIP_LOCKED,
    }

    private static class ReadVariant {
        private final LockLevel lockLevel;
        private final WaitingMode waitingMode;

        public ReadVariant(LockLevel lockLevel, WaitingMode waitingMode) {
            this.lockLevel = Objects.requireNonNull(lockLevel);
            this.waitingMode = Objects.requireNonNull(waitingMode);
        }

        public LockLevel getLockLevel() {
            return lockLevel;
        }

        public WaitingMode getWaitingMode() {
            return waitingMode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ReadVariant readVariant = (ReadVariant) o;

            if (lockLevel != readVariant.lockLevel) return false;
            return waitingMode == readVariant.waitingMode;
        }

        @Override
        public int hashCode() {
            int result = lockLevel.hashCode();
            result = 31 * result + waitingMode.hashCode();
            return result;
        }
    }

    @SafeVarargs
    public static <T> DaoRecordPgCrudOperations<T> create(
            JdbcTemplate jdbcTemplate,
            String table,
            DaoSqlTupleType<T> daoSqlTupleType,
            DaoSqlColumn<T, ?>... idColumns
    ) {
        Template<T> template = new Template<>();
        template.setUpValuesForDaoRecordCrudOperations(
                jdbcTemplate, table, daoSqlTupleType, idColumns
        );
        template.setUpValuesForDaoRecordPqCrudOperations();
        return new DaoRecordPgCrudOperations<>(template);
    }

    protected static class Template<T> extends DaoRecordCrudOperations.Template<T> {

        private String sqlCreateWithGeneratedId;
        private String sqlCreateIfNotExists;
        private Map<ReadVariant, String> sqlReadVariants;

        protected String getSqlCreateWithGeneratedId() {
            return sqlCreateWithGeneratedId;
        }

        protected String getSqlCreateIfNotExists() {
            return sqlCreateIfNotExists;
        }

        protected Map<ReadVariant, String> getSqlReadVariants() {
            return sqlReadVariants;
        }

        public final void setUpValuesForDaoRecordPqCrudOperations() {
            sqlCreateWithGeneratedId = buildSqlCreateWithGeneratedId();
            sqlCreateIfNotExists = buildSqlCreateIfNotExists();
            sqlReadVariants = buildSqlReadVariants();
        }

        @Override
        protected String quoteSqlName(String name) {
            return PgSqls.quoteName(name);
        }

        private String buildSqlCreateWithGeneratedId() {
            return "INSERT INTO " + getTable() + " (" +
                    getDataColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .collect(Collectors.joining(", ")) +
                    ")  VALUES (" +
                    getDataColumns().stream()
                            .map(__ -> "?")
                            .collect(Collectors.joining(", ")) +
                    ") RETURNING " +
                    getIdColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .collect(Collectors.joining(", "));
        }

        private String buildSqlCreateIfNotExists() {
            return "INSERT INTO " + getTable() + " (" +
                    Stream.concat(
                            getIdColumns().stream(),
                            getDataColumns().stream()
                    )
                            .map(DaoSqlColumn::getColumnName)
                            .collect(Collectors.joining(", ")) +
                    ") VALUES (" +
                    Stream.concat(
                            getIdColumns().stream(),
                            getDataColumns().stream()
                    )
                            .map(__ -> "?")
                            .collect(Collectors.joining(", ")) +
                    ") ON CONFLICT (" +
                    getIdColumns().stream()
                            .map(DaoSqlColumn::getColumnName)
                            .collect(Collectors.joining(", ")) +
                    ") DO NOTHING";
        }

        private Map<ReadVariant, String> buildSqlReadVariants() {
            String base = "SELECT " +
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

            Map<ReadVariant, String> sqlReadVariants = new HashMap<>();

            StringBuilder sb = new StringBuilder();

            for (LockLevel lockLevel : LockLevel.values()) {
                for (WaitingMode waitingMode : WaitingMode.values()) {
                    ReadVariant variant = new ReadVariant(lockLevel, waitingMode);

                    sb.setLength(0);
                    sb.append(base);

                    switch (lockLevel) {
                        case NONE:
                            break;
                        case UPDATE:
                            sb.append(" FOR UPDATE");
                        case NO_ID_UPDATE:
                            sb.append(" FOR NO KEY UPDATE");
                            break;
                        case SHARE:
                            sb.append(" FOR SHARE");
                            break;
                        case ID_SHARE:
                            sb.append(" FOR KEY SHARE");
                            break;
                    }

                    switch (waitingMode) {
                        case WAIT:
                            break;
                        case NO_WAIT:
                            sb.append(" NO WAIT");
                            break;
                        case SKIP_LOCKED:
                            sb.append(" SKIP LOCKED");
                            break;
                    }

                    sqlReadVariants.put(variant, sb.toString());
                }
            }

            return Collections.unmodifiableMap(sqlReadVariants);
        }
    }
}
