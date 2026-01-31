package io.github.archipelagominecraft.buildplugin

import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.github.archipelagominecraft.buildplugin.configs.commonConfiguration
import io.github.archipelagominecraft.buildplugin.configs.modLoaderConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import java.util.*


@Suppress("unused")
abstract class BuildMultiversionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("buildMultiversion", BuildMultiversionExtension::class.java)
        commonConfiguration(target)

        // Loads custom properties for the specific version
// Everything should already be defined in VersionProperties.kt, but if some project needs to override something,
// it should do so in versions/dependencies/<mcVersion>.properties
        target.extensions.configure<StonecutterBuildExtension> {
            loadSpecificDependencyVersions(
                target, extension.versionPropertiesFolder
                    .convention(target.rootProject.layout.projectDirectory.dir("versionInfos")), current.version
            )
        }

        val javaVersion = target.provider {target.modInfo.javaVersion}

        modLoaderConfiguration(
            target,
            javaVersion,
            target.provider { target.loader },
        )
    }
}


/**
 * Loads all the properties from $projectDir/versions/dependencies/$minecraftVersion.properties if it exists
 *
 * You *could* also put them in $projectDir/versions/<minecraftVersion>-<loader>/gradle.properties, but this is less
 * nested directories
 */
private fun loadSpecificDependencyVersions(project: Project, folder: DirectoryProperty, minecraftVersion: String) {
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
