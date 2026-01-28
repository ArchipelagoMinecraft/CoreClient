package io.github.archipelagominecraft.plugin.configs

import ModLoaders
import PluginTypes
import io.archipelagominecraft.gradle.*
import io.github.archipelagominecraft.plugin.BuildMultiversionExtension
import io.github.archipelagominecraft.plugin.RunConfigurationData
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider


data class SpecificPluginApplicationResult(
    val modDependenciesConfiguration: Provider<Configuration>,
)

/**
 * This file either applies retrofuturagradle or modstitch, depending on what is configured for the version
 */
fun modLoaderConfiguration(
    project: Project,
    extension: BuildMultiversionExtension,
    javaVersion: Provider<Int>,
    modInfo: Provider<ModInfo>,
    loader: Provider<ModLoaders>,
    pluginType: Provider<PluginTypes>,
    runs: NamedDomainObjectSet<RunConfigurationData>,
): SpecificPluginApplicationResult {

    return when (pluginType.get()) {
        PluginTypes.RFG -> retroFuturaGradleConfiguration(project,javaVersion,extension,runs)


        PluginTypes.MODSTITCH -> modstitchConfiguration(project, javaVersion,loader,runs)
    }
}

