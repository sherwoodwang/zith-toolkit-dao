package org.zith.toolkit.dao.build.dsl;

import org.zith.toolkit.dao.build.generator.DaoSourceGenerator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

class Compilation {
    private final DaoSourceGenerator daoSourceGenerator;
    private final File destroot;
    private final HashMap<UnitKey, Unit> units;
    private final HashMap<String, TypeState> typeRegistry;

    Compilation(DaoSourceGenerator daoSourceGenerator, File destroot) {
        this.daoSourceGenerator = daoSourceGenerator;
        this.destroot = destroot;
        units = new HashMap<>();
        typeRegistry = new HashMap<>();
    }

    Unit unit(File file) {
        return units.computeIfAbsent(new FileUnitKey(file), key -> new Unit(key, file));
    }

    void register(TypeInformation typeInformation) {
        typeRegistry.put(typeInformation.getName(), new TypeState(typeInformation));
    }

    void emit(Unit unit) throws IOException {
        for (TypeState typeState : typeRegistry.values()) {
            if (typeState.typeInformation.getLocation() == unit) {
                typeState.emit();
            }
        }
    }

    TypeInformation lookup(String typeName) {
        TypeState typeState = typeRegistry.get(typeName);

        if (typeState == null) {
            return null;
        }

        return typeState.typeInformation;
    }

    class Unit {
        private final UnitKey key;
        private final File file;

        Unit(UnitKey key, File file) {
            this.key = key;
            this.file = file;
        }

        Compilation getCompilation() {
            return Compilation.this;
        }

        File getFile() {
            return file;
        }
    }

    private class UnitKey {
    }

    private class FileUnitKey extends UnitKey {
        private final File file;

        private FileUnitKey(File file) {
            this.file = file.getAbsoluteFile();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileUnitKey that = (FileUnitKey) o;

            return file.equals(that.file);
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }
    }

    private class TypeState {
        private final TypeInformation typeInformation;

        private TypeState(TypeInformation typeInformation) {
            this.typeInformation = typeInformation;
        }

        void emit() throws IOException {
            switch (typeInformation.getCategory()) {
                case RECORD:
                    daoSourceGenerator.generateRecordClass(typeInformation.getRecordDefinition())
                            .writeTo(destroot);
                    break;
                case TUPLE:
                    daoSourceGenerator.generateTupleClass(typeInformation.getTupleRecordDefinition())
                            .writeTo(destroot);
                    break;
                case TYPE_HANDLER_DICTIONARY:
                    daoSourceGenerator
                            .generateTypeHandlerDictionaryClass(
                                    typeInformation.getTypeHandlerDictionaryDefinition()
                            )
                            .writeTo(destroot);
                    break;
            }
        }
    }
}
