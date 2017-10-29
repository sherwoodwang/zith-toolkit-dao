package org.zith.toolkit.dao.build.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory

class DefaultZithToolkitDaoSourceSet {
    private final String name
    private final SourceDirectorySetFactory sourceDirectorySetFactory
    private final SourceDirectorySet dao

    DefaultZithToolkitDaoSourceSet(String name, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.name = name
        this.sourceDirectorySetFactory = sourceDirectorySetFactory

        this.dao = this.sourceDirectorySetFactory.create(name)
    }

    SourceDirectorySet getDao() {
        return dao
    }
}
