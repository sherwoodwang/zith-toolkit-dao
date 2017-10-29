package org.zith.toolkit.dao.build.gradle

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*
import org.zith.toolkit.dao.build.dsl.DaoRecordSchemaCompiler

class GenerateDaoTask extends SourceTask {
    @OutputDirectory
    File destinationDir


    @Override
    @PathSensitive(PathSensitivity.NAME_ONLY)
    FileTree getSource() {
        return super.getSource()
    }

    @TaskAction
    void generateDao() {
        def compiler = new DaoRecordSchemaCompiler()

        compiler.compile source.files, destinationDir
    }
}