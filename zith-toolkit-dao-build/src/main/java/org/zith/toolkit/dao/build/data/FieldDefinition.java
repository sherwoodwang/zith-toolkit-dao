package org.zith.toolkit.dao.build.data;

import com.google.common.base.CaseFormat;

public class FieldDefinition {
    private final String fieldName;
    private final String memberName;
    private final String accessorName;
    private final String type;

    public FieldDefinition(
            String fieldName,
            String memberName,
            String accessorName,
            String type
    ) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Missing fieldName");
        }

        if (memberName == null) {
            throw new IllegalArgumentException("Missing memberName");
        }

        if (accessorName == null) {
            throw new IllegalArgumentException("Missing accessorName");
        }

        this.fieldName = fieldName;
        this.memberName = memberName;
        this.accessorName = accessorName;
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getAccessorName() {
        return accessorName;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getType() {
        return type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fieldName;
        private String accessorName;
        private String memberName;
        private String type;

        private Builder() {
        }

        public Builder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder setAccessorName(String accessorName) {
            this.accessorName = accessorName;
            return this;
        }

        public Builder setMemberName(String memberName) {
            this.memberName = memberName;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public FieldDefinition build() {
            if (accessorName == null) {
                inferAccessorNameFromFieldName();
            }

            if (memberName == null) {
                inferMemberNameFromFieldName();
            }

            return new FieldDefinition(
                    fieldName,
                    memberName,
                    accessorName,
                    type
            );
        }

        private void inferAccessorNameFromFieldName() {
            if (fieldName != null) {
                accessorName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
            }
        }

        private void inferMemberNameFromFieldName() {
            if (fieldName != null) {
                memberName = "m_" + fieldName;
            }
        }

    }
}
