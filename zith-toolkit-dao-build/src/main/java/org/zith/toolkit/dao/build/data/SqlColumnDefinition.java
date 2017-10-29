package org.zith.toolkit.dao.build.data;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SqlColumnDefinition {
    private final String column;
    private final SqlTypeDetonator type;
    private final List<Integer> precision;
    private final String typeHandler;

    private final String fieldName;
    private final String memberName;
    private final String accessorName;

    public SqlColumnDefinition(
            String column,
            SqlTypeDetonator type,
            @Nullable List<Integer> precision,
            @Nullable String typeHandler,
            @Nullable String fieldName,
            @Nullable String memberName,
            @Nullable String accessorName
    ) {
        this.column = Objects.requireNonNull(column);
        this.type = Objects.requireNonNull(type);
        this.precision = precision == null ? null : ImmutableList.copyOf(precision);
        this.typeHandler = typeHandler;
        this.fieldName = fieldName;
        this.memberName = memberName;
        this.accessorName = accessorName;
    }

    public String getColumn() {
        return column;
    }

    public SqlTypeDetonator getType() {
        return type;
    }

    @Nullable
    public List<Integer> getPrecision() {
        return precision;
    }

    @Nullable
    public Optional<String> getTypeHandler() {
        return Optional.ofNullable(typeHandler);
    }

    @Nullable
    public String getFieldName() {
        return fieldName;
    }

    @Nullable
    public String getMemberName() {
        return memberName;
    }

    @Nullable
    public String getAccessorName() {
        return accessorName;
    }

    public ColumnFieldDefinition resolve(SqlTypeDictionaryDefinition dictionaryDefinition) {
        FieldDefinition fieldDefinition;
        SqlTypeHandlerDeclaration typeHandler;
        int jdbcType;
        {
            if (this.typeHandler == null) {
                typeHandler = dictionaryDefinition.getTypeHandlerDeclaration(type);

                if (typeHandler == null) {
                    throw new IllegalArgumentException("Failed to find a type handler for " + type);
                }
            } else {
                typeHandler = dictionaryDefinition.getTypeHandlerDeclaration(this.typeHandler);

                if (typeHandler == null) {
                    throw new IllegalArgumentException(
                            "Failed to find a type handler which name is " + this.typeHandler);
                }
            }

            jdbcType = typeHandler.getTypeSelectors().stream()
                    .filter(ts -> ts.getRootSqlType().equals(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Designated handler " + typeHandler + " doesn't support " + type))
                    .getJdbcType();

            FieldDefinition.Builder builder = FieldDefinition.builder()
                    .setFieldName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.toLowerCase()))
                    .setType(typeHandler.getType());

            if (fieldName != null) {
                builder.setFieldName(fieldName);
            }

            if (memberName != null) {
                builder.setMemberName(memberName);
            }

            if (accessorName != null) {
                builder.setFieldName(accessorName);
            }

            fieldDefinition = builder.build();
        }

        return new ColumnFieldDefinition(
                column,
                type,
                precision,
                fieldDefinition,
                typeHandler,
                jdbcType
        );
    }

    public static class ColumnFieldDefinition {
        private final String column;
        private final SqlTypeDetonator type;
        private final List<Integer> precision;
        private final FieldDefinition fieldDefinition;
        private final SqlTypeHandlerDeclaration typeHandler;
        private final int jdbcType;

        private ColumnFieldDefinition(
                String column,
                SqlTypeDetonator type,
                List<Integer> precision,
                FieldDefinition fieldDefinition,
                SqlTypeHandlerDeclaration typeHandler,
                int jdbcType
        ) {
            this.column = column;
            this.type = type;
            this.precision = precision;
            this.fieldDefinition = fieldDefinition;
            this.typeHandler = typeHandler;
            this.jdbcType = jdbcType;
        }

        public String getColumn() {
            return column;
        }

        public SqlTypeDetonator getType() {
            return type;
        }

        public List<Integer> getPrecision() {
            return precision;
        }

        public FieldDefinition getFieldDefinition() {
            return fieldDefinition;
        }

        public SqlTypeHandlerDeclaration getTypeHandler() {
            return typeHandler;
        }

        public int getJdbcType() {
            return jdbcType;
        }
    }
}
