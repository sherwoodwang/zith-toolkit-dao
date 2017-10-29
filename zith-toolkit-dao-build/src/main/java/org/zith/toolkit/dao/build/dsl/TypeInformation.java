package org.zith.toolkit.dao.build.dsl;

import com.google.common.collect.ImmutableList;
import org.zith.toolkit.dao.build.data.RecordDefinition;
import org.zith.toolkit.dao.build.data.SqlTupleDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;

import java.util.List;
import java.util.Objects;

class TypeInformation {
    private final Compilation.Unit location;
    private final String name;
    private final List<String> dependencies;
    private final Category category;
    private final Object definition;

    private TypeInformation(
            Compilation.Unit location,
            String name,
            List<String> dependencies,
            Category category,
            Object definition
    ) {
        this.location = Objects.requireNonNull(location);
        this.name = name;
        this.dependencies = ImmutableList.copyOf(dependencies);
        this.category = Objects.requireNonNull(category);
        this.definition = Objects.requireNonNull(definition);
    }

    TypeInformation(
            Compilation.Unit location,
            String name,
            List<String> dependencies,
            RecordDefinition recordDefinition
    ) {
        this(location, name, dependencies, Category.RECORD, recordDefinition);
    }

    TypeInformation(
            Compilation.Unit location,
            String name,
            List<String> dependencies,
            SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition
    ) {
        this(location, name, dependencies, Category.TUPLE, tupleRecordDefinition);
    }

    TypeInformation(
            Compilation.Unit location,
            String name,
            List<String> dependencies,
            SqlTypeDictionaryDefinition sqlTypeDictionaryDefinition
    ) {
        this(location, name, dependencies, Category.TYPE_HANDLER_DICTIONARY, sqlTypeDictionaryDefinition);
    }

    String getName() {
        return name;
    }

    Compilation.Unit getLocation() {
        return location;
    }

    Category getCategory() {
        return category;
    }

    RecordDefinition getRecordDefinition() {
        return (RecordDefinition) definition;
    }

    SqlTupleDefinition.TupleRecordDefinition getTupleRecordDefinition() {
        return (SqlTupleDefinition.TupleRecordDefinition) definition;
    }

    SqlTypeDictionaryDefinition getTypeHandlerDictionaryDefinition() {
        return (SqlTypeDictionaryDefinition) definition;
    }

    enum Category {
        RECORD,
        TUPLE, TYPE_HANDLER_DICTIONARY
    }
}
