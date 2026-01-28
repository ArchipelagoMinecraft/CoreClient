package io.github.archipelagominecraft.plugin

import Keys
import PluginTypes
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.requiredProp
import io.github.archipelagominecraft.plugin.configs.commonConfiguration
import io.github.archipelagominecraft.plugin.configs.modLoaderConfiguration
import org.gradle.api.JavaVersion
import org.gradle.api.Named
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import xyz.wagyourtail.jvmdg.gradle.JVMDowngraderExtension
import xyz.wagyourtail.jvmdg.gradle.JVMDowngraderPlugin
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.util.*

enum class Side {
    Client, Server
}

data class RunConfigurationData(
    private val name: String,
    val side: Provider<Side>,
    val workingDirectory: Provider<Directory>,
    val args: Provider<List<String>>,
//    val classpath: ClasspathEntr
) : Named {
    override fun getName(): String = name
}

@Suppress("unused")
abstract class BuildMultiversionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("buildMultiversion", BuildMultiversionExtension::class.java)
        commonConfiguration(target)
        jvmDowngrader(target, extension)

        // Loads custom properties for the specific version
// Everything should already be defined in VersionProperties.kt, but if some project needs to override something,
// it should do so in versions/dependencies/<mcVersion>.properties
        target.extensions.configure<StonecutterBuildExtension> {
            loadSpecificDependencyVersions(
                target, extension.versionPropertiesFolder
                    .convention(target.rootProject.layout.projectDirectory.dir("versionInfos")), current.version
            )
        }
        val defaultRuns = defaultRunConfigurations(target)
        extension.runs.addAllLater(
            extension.createDefaultRuns.flatMap { if (it) defaultRuns else target.provider { emptySet() } }
        )

        val javaVersion = extension.forceJavaVersion.orElse(target.modInfo.javaVersion)

        val pluginType = target.requiredProp(Keys.pluginType).let(PluginTypes::parse)
        val result = modLoaderConfiguration(
            target,
            extension,
            javaVersion,
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

private fun jvmDowngrader(target: Project, extension: BuildMultiversionExtension) {
    if (extension.enableJvmDowngrader.get()) {
        target.pluginManager.apply(JVMDowngraderPlugin::class.java)
        target.tasks.named("assemble") {
            dependsOn(target.tasks.named("shadeDowngradedApi"))
        }

        val extension = target.extensions.configure<JVMDowngraderExtension> {
            downgradeTo.set(JavaVersion.toVersion(target.modInfo.javaVersion))
        }
        val implementation = target.configurations.named("implementation")
        target.configurations.register("downgradeImplementation") {
            val downgrade = this
            implementation.get().apply {
//                todo replace all "implementation" with constant
                extendsFrom(downgrade)
            }
            target.extensions.configure<JVMDowngraderExtension>() {
                dg(downgrade,true) {
                    downgradeTo.set(JavaVersion.toVersion(target.modInfo.javaVersion))
                    this@dg.logLevel.set("DEBUG")
                }
            }
        }
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

