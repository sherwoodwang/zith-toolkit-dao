package org.zith.toolkit.dao.build.data;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SqlTypeDictionaryDefinition {

    private final String name;
    private final String packageName;
    private final List<SqlTypeHandlerDeclaration> sqlTypeHandlerDeclarations;

    private final Map<String, SqlTypeHandlerDeclaration> typeHandlerDeclarationsByName;

    public SqlTypeDictionaryDefinition(
            String name,
            String packageName,
            List<SqlTypeHandlerDeclaration> sqlTypeHandlerDeclarations
    ) {
        this.name = Objects.requireNonNull(name);
        this.packageName = Objects.requireNonNull(packageName);
        this.sqlTypeHandlerDeclarations = ImmutableList.copyOf(sqlTypeHandlerDeclarations);

        typeHandlerDeclarationsByName = Collections.unmodifiableMap(this.sqlTypeHandlerDeclarations.stream()
                .collect(Collectors.toMap(SqlTypeHandlerDeclaration::getName, UnaryOperator.identity())));
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<SqlTypeHandlerDeclaration> getSqlTypeHandlerDeclarations() {
        return sqlTypeHandlerDeclarations;
    }

    public Optional<SqlTypeHandlerDeclaration> getTypeHandlerDeclaration(String name) {
        return Optional.ofNullable(typeHandlerDeclarationsByName.get(name));
    }

    public String getQualifiedName() {
        return getPackageName() + '.' + getName();
    }
}
