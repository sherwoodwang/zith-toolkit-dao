package org.zith.toolkit.dao.util.spring;

import org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.zith.toolkit.dao.support.DaoSqlColumn;
import org.zith.toolkit.dao.support.DaoSqlTupleType;
import org.zith.toolkit.dao.support.DaoSqlTypeHandler;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaoRecordPgCrudOperations<T> extends DaoRecordCrudOperations<T> {

    private final String createWithNewIdSql;
    private final String createIfNotExists;

    private DaoRecordPgCrudOperations(
            Template<T> template
    ) {
        super(template);

        createWithNewIdSql = template.createWithNewIdSql;
        createIfNotExists = template.createIfNotExists;
    }

    public String getCreateWithNewIdSql() {
        return createWithNewIdSql;
    }

    public String getCreateIfNotExists() {
        return createIfNotExists;
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
                    PreparedStatement ps = con.prepareStatement(getCreateWithNewIdSql(), idColumnNames);
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

    public void createIfNotExists(T record) {
        getJdbcTemplate().update(
                getCreateIfNotExists(),
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
        private static final Set<String> KEYWORDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                "abort", "absolute", "access", "action", "add", "admin", "after", "aggregate", "all", "also", "alter",
                "always", "analyse", "analyze", "and", "any", "array", "as", "asc", "assertion", "assignment",
                "asymmetric", "at", "attach", "attribute", "authorization", "backward", "before", "begin", "between",
                "bigint", "binary", "bit", "boolean", "both", "by", "cache", "called", "cascade", "cascaded", "case",
                "cast", "catalog", "chain", "char", "character", "characteristics", "check", "checkpoint", "class",
                "close", "cluster", "coalesce", "collate", "collation", "column", "columns", "comment", "comments",
                "commit", "committed", "concurrently", "configuration", "conflict", "connection", "constraint",
                "constraints", "content", "continue", "conversion", "copy", "cost", "create", "cross", "csv", "cube",
                "current", "current_catalog", "current_date", "current_role", "current_schema", "current_time",
                "current_timestamp", "current_user", "cursor", "cycle", "data", "database", "day", "deallocate", "dec",
                "decimal", "declare", "default", "defaults", "deferrable", "deferred", "definer", "delete", "delimiter",
                "delimiters", "depends", "desc", "detach", "dictionary", "disable", "discard", "distinct", "do",
                "document", "domain", "double", "drop", "each", "else", "enable", "encoding", "encrypted", "end",
                "enum", "escape", "event", "except", "exclude", "excluding", "exclusive", "execute", "exists",
                "explain", "extension", "external", "extract", "false", "family", "fetch", "filter", "first", "float",
                "following", "for", "force", "foreign", "forward", "freeze", "from", "full", "function", "functions",
                "generated", "global", "grant", "granted", "greatest", "group", "grouping", "handler", "having",
                "header", "hold", "hour", "identity", "if", "ilike", "immediate", "immutable", "implicit", "import",
                "in", "including", "increment", "index", "indexes", "inherit", "inherits", "initially", "inline",
                "inner", "inout", "input", "insensitive", "insert", "instead", "int", "integer", "intersect",
                "interval", "into", "invoker", "is", "isnull", "isolation", "join", "key", "label", "language", "large",
                "last", "lateral", "leading", "leakproof", "least", "left", "level", "like", "limit", "listen", "load",
                "local", "localtime", "localtimestamp", "location", "lock", "locked", "logged", "mapping", "match",
                "materialized", "maxvalue", "method", "minute", "minvalue", "mode", "month", "move", "name", "names",
                "national", "natural", "nchar", "new", "next", "no", "none", "not", "nothing", "notify", "notnull",
                "nowait", "null", "nullif", "nulls", "numeric", "object", "of", "off", "offset", "oids", "old", "on",
                "only", "operator", "option", "options", "or", "order", "ordinality", "out", "outer", "over",
                "overlaps", "overlay", "overriding", "owned", "owner", "parallel", "parser", "partial", "partition",
                "passing", "password", "placing", "plans", "policy", "position", "preceding", "precision", "prepare",
                "prepared", "preserve", "primary", "prior", "privileges", "procedural", "procedure", "program",
                "publication", "quote", "range", "read", "real", "reassign", "recheck", "recursive", "ref",
                "references", "referencing", "refresh", "reindex", "relative", "release", "rename", "repeatable",
                "replace", "replica", "reset", "restart", "restrict", "returning", "returns", "revoke", "right", "role",
                "rollback", "rollup", "row", "rows", "rule", "savepoint", "schema", "schemas", "scroll", "search",
                "second", "security", "select", "sequence", "sequences", "serializable", "server", "session",
                "session_user", "set", "setof", "sets", "share", "show", "similar", "simple", "skip", "smallint",
                "snapshot", "some", "sql", "stable", "standalone", "start", "statement", "statistics", "stdin",
                "stdout", "storage", "strict", "strip", "subscription", "substring", "symmetric", "sysid", "system",
                "table", "tables", "tablesample", "tablespace", "temp", "template", "temporary", "text", "then", "time",
                "timestamp", "to", "trailing", "transaction", "transform", "treat", "trigger", "trim", "true",
                "truncate", "trusted", "type", "types", "unbounded", "uncommitted", "unencrypted", "union", "unique",
                "unknown", "unlisten", "unlogged", "until", "update", "user", "using", "vacuum", "valid", "validate",
                "validator", "value", "values", "varchar", "variadic", "varying", "verbose", "version", "view", "views",
                "volatile", "when", "where", "whitespace", "window", "with", "within", "without", "work", "wrapper",
                "write", "xml", "xmlattributes", "xmlconcat", "xmlelement", "xmlexists", "xmlforest", "xmlnamespaces",
                "xmlparse", "xmlpi", "xmlroot", "xmlserialize", "xmltable", "year", "yes", "zone"
        )));
        private static final Pattern PLAIN_NAME = Pattern.compile("[a-z][a-z0-9]*");

        private String createWithNewIdSql;
        private String createIfNotExists;

        public final void setUpValuesForDaoRecordPqCrudOperations() {
            createWithNewIdSql = buildCreateWithGeneratedIdSql();
            createIfNotExists = buildCreateIfNotExistsSql();
        }

        @Override
        protected String quoteSqlName(String name) {
            if (KEYWORDS.contains(name) || !name.equals(name.toLowerCase()) || !PLAIN_NAME.matcher(name).matches()) {
                return '"' + name + '"';
            } else {
                return name;
            }
        }

        private String buildCreateWithGeneratedIdSql() {
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

        private String buildCreateIfNotExistsSql() {
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
    }
}
