package org.zith.toolkit.dao.build.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention

import javax.inject.Inject

class ZithToolkitDaoPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(ZithToolkitDaoBasePlugin)
        project.plugins.apply(JavaPlugin)
    }
}