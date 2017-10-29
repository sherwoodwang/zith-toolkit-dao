package org.zith.toolkit.dao.build.data;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlTupleDefinition {
    private final String name;
    private final String packageName;
    private final List<SqlColumnDefinition> columns;

    public SqlTupleDefinition(String name, String packageName, List<SqlColumnDefinition> columns) {
        this.name = Objects.requireNonNull(name);
        this.packageName = Objects.requireNonNull(packageName);
        this.columns = ImmutableList.copyOf(columns);
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<SqlColumnDefinition> getColumns() {
        return columns;
    }

    public TupleRecordDefinition resolve(SqlTypeDictionaryDefinition sqlTypeDictionaryDefinition) {
        List<SqlColumnDefinition.ColumnFieldDefinition> columnFieldDefinitions = columns.stream()
                .map(c -> c.resolve(sqlTypeDictionaryDefinition))
                .collect(Collectors.toList());

        RecordDefinition.Builder builder = RecordDefinition.builder()
                .setName(name)
                .setPackageName(packageName);

        columnFieldDefinitions.stream()
                .map(SqlColumnDefinition.ColumnFieldDefinition::getFieldDefinition)
                .forEach(builder::addField);

        RecordDefinition recordDefinition = builder.build();

        return new TupleRecordDefinition(
                recordDefinition,
                columnFieldDefinitions,
                sqlTypeDictionaryDefinition
        );
    }

    public static class TupleRecordDefinition {
        private final RecordDefinition recordDefinition;
        private final List<SqlColumnDefinition.ColumnFieldDefinition> columnFieldDefinitions;
        private final SqlTypeDictionaryDefinition sqlTypeDictionaryDefinition;

        private TupleRecordDefinition(
                RecordDefinition recordDefinition,
                List<SqlColumnDefinition.ColumnFieldDefinition> columnFieldDefinitions,
                SqlTypeDictionaryDefinition sqlTypeDictionaryDefinition
        ) {
            this.recordDefinition = recordDefinition;
            this.columnFieldDefinitions = ImmutableList.copyOf(columnFieldDefinitions);
            this.sqlTypeDictionaryDefinition = sqlTypeDictionaryDefinition;
        }

        public RecordDefinition getRecordDefinition() {
            return recordDefinition;
        }

        public List<SqlColumnDefinition.ColumnFieldDefinition> getColumnFieldDefinitions() {
            return columnFieldDefinitions;
        }

        public SqlTypeDictionaryDefinition getSqlTypeDictionaryDefinition() {
            return sqlTypeDictionaryDefinition;
        }
    }
}
