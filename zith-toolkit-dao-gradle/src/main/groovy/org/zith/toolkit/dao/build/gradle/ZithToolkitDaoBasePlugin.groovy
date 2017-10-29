package org.zith.toolkit.dao.build.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention

import javax.inject.Inject

class ZithToolkitDaoBasePlugin implements Plugin<Project> {
    private final SourceDirectorySetFactory sourceDirectorySetFactory

    @Inject
    ZithToolkitDaoBasePlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.sourceDirectorySetFactory = sourceDirectorySetFactory
    }

    @Override
    void apply(Project project) {
        project.with {
            plugins.apply(JavaBasePlugin.class)

            def javaConvention = convention.getPlugin(JavaPluginConvention)

            javaConvention.sourceSets.each { sourceSet ->
                def sourceSetPrefix = 'main' == sourceSet.name ?
                        '' : sourceSet.name.capitalize()

                def daoSourceSet = new DefaultZithToolkitDaoSourceSet(
                        ((DefaultSourceSet) sourceSet).displayName,
                        sourceDirectorySetFactory
                )

                daoSourceSet.dao.srcDir 'src/' + sourceSet.name + '/dao'
                def generatedSourceDirectory = new File(buildDir, 'generated-src/dao/' + sourceSet.name + '/java')

                def daoGenerationTask = tasks.create('generate' + sourceSetPrefix + 'Dao', GenerateDaoTask) {
                    source daoSourceSet.dao
                    destinationDir = generatedSourceDirectory
                    include '**/*.record'
                }

                sourceSet.java {
                    srcDir generatedSourceDirectory
                }

                tasks.getByName(sourceSet.compileJavaTaskName) {
                    dependsOn daoGenerationTask
                }
            }
        }
    }
}
