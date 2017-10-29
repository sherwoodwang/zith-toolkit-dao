package org.zith.toolkit.dao.build.dsl;

import com.google.common.collect.ImmutableList;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;
import org.zith.toolkit.dao.build.dsl.parser.JavaFarReferenceElement;
import org.zith.toolkit.dao.build.dsl.parser.JavaReferenceElement;

import java.util.HashMap;
import java.util.List;

class ScopeContext {
    private final Compilation.Unit unit;
    private final List<String> currentPackage;
    private final HashMap<String, List<String>> localNames;
    private SqlTypeDictionaryDefinition currentTypeHandlerDictionary;

    ScopeContext(Compilation.Unit unit, List<String> currentPackage) {
        this.unit = unit;
        this.currentPackage = ImmutableList.copyOf(currentPackage);
        this.localNames = new HashMap<>();
        this.currentTypeHandlerDictionary = null;
    }

    ScopeContext(ScopeContext parent, ImmutableList<String> currentPackage) {
        this.unit = parent.unit;
        this.currentPackage = currentPackage;
        this.localNames = new HashMap<>(parent.localNames);
        this.currentTypeHandlerDictionary = parent.currentTypeHandlerDictionary;
    }

    List<String> resolve(JavaFarReferenceElement javaFarReferenceElement) {
        if (javaFarReferenceElement.getUpper() == -1) {
            List<String> names = javaFarReferenceElement.getNames();
            String firstName = names.get(0);
            if (localNames.containsKey(firstName)) {
                return ImmutableList.<String>builder()
                        .addAll(localNames.get(firstName))
                        .addAll(names.subList(1, names.size()))
                        .build();
            } else {
                return names;
            }
        } else {
            if (getCurrentPackage().size() < javaFarReferenceElement.getUpper()) {
                throw new IllegalArgumentException("Cannot resolve " + javaFarReferenceElement + ": too many leading dots");
            }

            return ImmutableList.<String>builder()
                    .addAll(getCurrentPackage().subList(0, getCurrentPackage().size() - javaFarReferenceElement.getUpper()))
                    .addAll(javaFarReferenceElement.getNames())
                    .build();
        }
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

    SqlTypeDictionaryDefinition getCurrentTypeHandlerDictionary() {
        return currentTypeHandlerDictionary;
    }

    void setCurrentTypeHandlerDictionary(SqlTypeDictionaryDefinition currentTypeHandlerDictionary) {
        this.currentTypeHandlerDictionary = currentTypeHandlerDictionary;
    }
}
