package org.zith.toolkit.dao.build.generator;

import com.squareup.javapoet.JavaFile;
import org.zith.toolkit.dao.build.data.RecordDefinition;
import org.zith.toolkit.dao.build.data.SqlTupleDefinition;
import org.zith.toolkit.dao.build.data.SqlTypeDictionaryDefinition;

public class DaoSourceGenerator {
    public JavaFile generateRecordClass(RecordDefinition recordDefinition) {
        return new RecordClassGenerator(recordDefinition).generate();
    }

    public JavaFile generateTypeHandlerDictionaryClass(
            SqlTypeDictionaryDefinition sqlTypeDictionaryDefinition) {
        return new TypeHandlerDictionaryClassGenerator(sqlTypeDictionaryDefinition).generate();
    }

    public JavaFile generateTupleClass(SqlTupleDefinition.TupleRecordDefinition tupleRecordDefinition) {
        return new TupleClassGenerator(tupleRecordDefinition).generate();
    }
}
