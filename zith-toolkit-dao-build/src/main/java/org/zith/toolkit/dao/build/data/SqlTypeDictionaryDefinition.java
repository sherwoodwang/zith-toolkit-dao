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
    private final Map<SqlTypeDetonator, SqlTypeHandlerDeclaration> typeHandlerDeclarationsBySqlType;

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
        typeHandlerDeclarationsBySqlType = Collections.unmodifiableMap(this.sqlTypeHandlerDeclarations.stream()
                .flatMap(d -> d.getTypeSelectors().stream()
                        .map(ts -> new AbstractMap.SimpleImmutableEntry<>(ts, d)))
                .sorted(Comparator.comparing(e -> e.getKey().getNice()))
                .map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey().getRootSqlType(), e.getValue()))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.reducing(null, (v1, v2) -> v1 != null ? v1 : v2)
                        )
                )));
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

    public SqlTypeHandlerDeclaration getTypeHandlerDeclaration(String name) {
        SqlTypeHandlerDeclaration sqlTypeHandlerDeclaration = typeHandlerDeclarationsByName.get(name);

        if (sqlTypeHandlerDeclaration == null) {
            throw new NoSuchElementException();
        }

        return sqlTypeHandlerDeclaration;
    }

    public SqlTypeHandlerDeclaration getTypeHandlerDeclaration(SqlTypeDetonator sqlType) {
        return typeHandlerDeclarationsBySqlType.get(sqlType);
    }

    public String getQualifiedName() {
        return getPackageName() + '.' + getName();
    }
}
