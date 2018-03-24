package org.zith.toolkit.dao.build.data;

import com.google.common.base.CaseFormat;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class SqlColumnDefinition {
    private final String column;
    private final String typeHandler;

    private final String fieldName;
    private final String memberName;
    private final String accessorName;

    public SqlColumnDefinition(
            String column,
            String typeHandler,
            @Nullable String fieldName,
            @Nullable String memberName,
            @Nullable String accessorName
    ) {
        this.column = Objects.requireNonNull(column);
        this.typeHandler = Objects.requireNonNull(typeHandler);
        this.fieldName = fieldName;
        this.memberName = memberName;
        this.accessorName = accessorName;
    }

    public String getColumn() {
        return column;
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
        SqlTypeHandlerDeclaration typeHandler =
                dictionaryDefinition.getTypeHandlerDeclaration(this.typeHandler)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Failed to find a type handler which name is " + this.typeHandler));

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
            builder.setAccessorName(accessorName);
        }

        fieldDefinition = builder.build();

        return new ColumnFieldDefinition(
                column,
                fieldDefinition,
                typeHandler
        );
    }

    public static class ColumnFieldDefinition {
        private final String column;
        private final FieldDefinition fieldDefinition;
        private final SqlTypeHandlerDeclaration typeHandler;

        private ColumnFieldDefinition(
                String column,
                FieldDefinition fieldDefinition,
                SqlTypeHandlerDeclaration typeHandler
        ) {
            this.column = column;
            this.fieldDefinition = fieldDefinition;
            this.typeHandler = typeHandler;
        }

        public String getColumn() {
            return column;
        }

        public FieldDefinition getFieldDefinition() {
            return fieldDefinition;
        }

        public SqlTypeHandlerDeclaration getTypeHandler() {
            return typeHandler;
        }

    }
}
