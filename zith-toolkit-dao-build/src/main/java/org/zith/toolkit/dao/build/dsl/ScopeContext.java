package org.zith.toolkit.dao.build.dsl;

import com.google.common.collect.ImmutableList;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;
import org.zith.toolkit.dao.build.dsl.element.JavaReferenceElement;

import java.util.HashMap;
import java.util.List;

class ScopeContext {
    private final Compilation.Unit unit;
    private final List<String> currentPackage;
    private final HashMap<String, List<String>> localNames;
    private SqlTypeDictionaryDefinition defaultTypeDictionary;

    ScopeContext(Compilation.Unit unit, List<String> currentPackage) {
        this.unit = unit;
        this.currentPackage = ImmutableList.copyOf(currentPackage);
        this.localNames = new HashMap<>();
        this.defaultTypeDictionary = null;
    }

    ScopeContext(ScopeContext parent, ImmutableList<String> currentPackage) {
        this.unit = parent.unit;
        this.currentPackage = currentPackage;
        this.localNames = new HashMap<>(parent.localNames);
        this.defaultTypeDictionary = parent.defaultTypeDictionary;
    }

    List<String> resolve(JavaReferenceElement javaReferenceElement) {
        List<String> names = javaReferenceElement.getNames();
        String firstName = names.get(0);
        if (localNames.containsKey(firstName)) {
            return ImmutableList.<String>builder()
                    .addAll(localNames.get(firstName))
                    .addAll(names.subList(1, names.size()))
                    .build();
        } else {
            return names;
        }
    }

    void registerLocalName(String name, List<String> path) {
        if (name == null) {
            name = path.get(path.size() - 1);
        }

        if (localNames.putIfAbsent(name, ImmutableList.copyOf(path)) != null) {
            throw new IllegalArgumentException("Conflict name: " + name);
        }
    }

    Compilation.Unit getUnit() {
        return unit;
    }

    List<String> getCurrentPackage() {
        return currentPackage;
    }

    SqlTypeDictionaryDefinition getDefaultSqlTypeDictionary() {
        return defaultTypeDictionary;
    }

    void setDefaultTypeDictionary(SqlTypeDictionaryDefinition defaultTypeDictionary) {
        this.defaultTypeDictionary = defaultTypeDictionary;
    }
}
