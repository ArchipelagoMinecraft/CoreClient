package io.github.archipelagominecraft.plugin

import Keys
import PluginTypes
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.requiredProp
import io.github.archipelagominecraft.plugin.configs.commonConfiguration
import io.github.archipelagominecraft.plugin.configs.modLoaderConfiguration
import org.gradle.api.Named
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import java.util.*

enum class Side {
    Client, Server
}

data class RunConfigurationData(
    private val name: String,
    val side: Provider<Side>,
    val workingDirectory: Provider<Directory>,
    val args: Provider<List<String>>,
) : Named {
    override fun getName(): String = name
}

@Suppress("unused")
abstract class BuildMultiversionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("buildMultiversion", BuildMultiversionExtension::class.java)
        commonConfiguration(target)

        // Loads custom properties for the specific version
// Everything should already be defined in VersionProperties.kt, but if some project needs to override something,
// it should do so in versions/dependencies/<mcVersion>.properties
        target.extensions.configure<StonecutterBuildExtension> {
            loadSpecificDependencyVersions(target,extension.versionPropertiesFolder
                .convention(target.rootProject.layout.projectDirectory.dir("versionInfos"))
                , current.version)
        }
        val defaultRuns = defaultRunConfigurations(target)
        extension.runs.addAllLater(
            extension.createDefaultRuns.flatMap { if (it) defaultRuns else target.provider { emptySet() } }
        )

        val pluginType = target.requiredProp(Keys.pluginType).let(PluginTypes::parse)
        val result = modLoaderConfiguration(
            target,
            extension,
            target.provider { target.modInfo },
            target.provider { target.loader },
            target.provider { pluginType },
            extension.runs
        )


// Create a "multiModImplementation" dependency configuration, used for depending on other mods
// and which maps to different configuration depending on if modstitch or retrofuturagradle are loaded
// This only creates the configuration, the actual mapping part will be in rfg-conventions.gradle.kts and
// modstitch-conventions.gradle.kts

        target.configurations.register("multiModImplementation") {
            isCanBeConsumed = false
            isCanBeResolved = true
            extendsFrom(result.modDependenciesConfiguration.get())
        }
    }

    private fun defaultRunConfigurations(
        target: Project,
    ): Provider<Set<RunConfigurationData>> = target.provider {
        setOf(
            RunConfigurationData(
                "client",
                target.provider { Side.Client },
                target.provider { target.layout.projectDirectory.dir("runClient") },
                target.provider { emptyList() }
            ),
            RunConfigurationData(
                "server",
                target.provider { Side.Server },
                target.provider { target.layout.projectDirectory.dir("runServer") },
                target.provider { listOf("nogui") }
            )
        )
    }
}

/**
 * Loads all the properties from $projectDir/versions/dependencies/$minecraftVersion.properties if it exists
 *
 * You *could* also put them in $projectDir/versions/<minecraftVersion>-<loader>/gradle.properties, but this is less
 * nested directories
 */
private fun loadSpecificDependencyVersions(project: Project,folder: DirectoryProperty, minecraftVersion: String) {
    val customPropsFile = folder.file("$minecraftVersion.properties").orNull?.asFile ?: return

    if (customPropsFile.exists()) {
        val customProps = Properties().apply {
            customPropsFile.inputStream().use { load(it) }
        }
        customProps.forEach { (key, value) ->
            project.extra[key.toString()] = value
        }
    }
}

