package io.github.archipelagominecraft.plugin.configs

import ModLoaders
import PluginTypes
import io.github.archipelagominecraft.plugin.RunConfigurationData
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.extra


data class SpecificPluginApplicationResult(
    val modDependenciesConfiguration: Provider<Configuration>,
)

/**
 * This file either applies retrofuturagradle or modstitch, depending on what is configured for the version
 */
fun modLoaderConfiguration(
    project: Project,
    javaVersion: Provider<Int>,
    loader: Provider<ModLoaders>,
    pluginType: Provider<PluginTypes>,
    runs: NamedDomainObjectSet<RunConfigurationData>,
) {

    when (pluginType.get()) {
        PluginTypes.RFG -> {
            retroFuturaGradleConfiguration(project)
        }
        else -> {
            project.extra["modstitch.platform"] = when (loader.get()) {
                ModLoaders.NEOFORGE -> "moddevgradle"
                ModLoaders.FORGE -> "moddevgradle-legacy"
                ModLoaders.FABRIC -> "fabric-loom-remap"
                ModLoaders.NONE_VANILLA -> "moddevgradle"
            }
            project.pluginManager.apply {
                apply("dev.isxander.modstitch.base")
            }
        }
    }

    modstitchConfiguration(project, javaVersion,loader,runs)

}

