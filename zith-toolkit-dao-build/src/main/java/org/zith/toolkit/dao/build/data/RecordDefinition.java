package org.zith.toolkit.dao.build.data;

import java.util.*;
import java.util.stream.Collectors;

public class RecordDefinition {

    private final String name;
    private final String packageName;
    private final List<FieldDefinition> fields;

    public RecordDefinition(
            String name,
            String packageName,
            List<FieldDefinition> fields
    ) {
        requiresNoDuplication(fields.stream().map(FieldDefinition::getFieldName).collect(Collectors.toList()));
        requiresNoDuplication(fields.stream().map(FieldDefinition::getAccessorName).collect(Collectors.toList()));

        this.name = name;
        this.packageName = packageName;
        this.fields = Collections.unmodifiableList(new ArrayList<>(fields));
    }

    private static <T> void requiresNoDuplication(Collection<T> collection) {
        Object[] objects = collection.stream().sorted().toArray();

        for (int i = 0; i < objects.length - 1; i++) {
            Object o0 = objects[i];
            Object o1 = objects[i + 1];

            if (Objects.equals(o0, o1)) {
                throw new IllegalArgumentException("Duplicated value: " + o1);
            }
        }
    }

    public String getQualifiedName() {
        return getPackageName() + '.' + getName();
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String packageName;
        private List<FieldDefinition> fields;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder addField(FieldDefinition fieldDefinition) {
            if (fields == null) {
                fields = new LinkedList<>();
            }

            fields.add(fieldDefinition);
            return this;
        }

        public RecordDefinition build() {
            if (fields == null) {
                fields = new LinkedList<>();
            }

            return new RecordDefinition(name, packageName, fields);
        }
    }
}
