package io.github.archipelagominecraft.buildplugin.configs

import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.isxander.modstitch.util.Platform
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.github.archipelagominecraft.buildplugin.ModLoaders
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


data class SpecificPluginApplicationResult(
    val modDependenciesConfiguration: Provider<Configuration>,
)

/**
 * This file applies modstitch, and fixes RetroFuturaGradle a bit
 */
fun modLoaderConfiguration(
    project: Project,
    javaVersion: Provider<Int>,
    loader: Provider<ModLoaders>,
) {

    val sc = project.extensions.getByType<StonecutterBuildExtension>()

    val platform = when (loader.get()) {
        ModLoaders.NEOFORGE -> Platform.MDG
        ModLoaders.FORGE if sc.current.parsed <= "1.12.2" -> Platform.RFG
        ModLoaders.FORGE -> Platform.MDGLegacy
        ModLoaders.FABRIC -> Platform.LoomRemap
        ModLoaders.NONE_VANILLA if sc.current.parsed <= "1.12.2" -> Platform.RFG
        ModLoaders.NONE_VANILLA if sc.current.parsed <= "1.21" -> Platform.MDGLegacy
        ModLoaders.NONE_VANILLA -> Platform.MDG
    }

    if (platform == Platform.RFG) {
        reflectionsRFGFix()
    }

    project.extra["modstitch.platform"] = platform.friendlyName
    project.pluginManager.apply("dev.isxander.modstitch.base")
    modstitchConfiguration(project, javaVersion, loader.get() == ModLoaders.NONE_VANILLA)
    if (platform == Platform.RFG) {
        rfgStonecutterCompat(project)
    }

}

// RetroFuturaGradle adds new source sets for it's minecraft tasks, and they can trigger
// task ordering errors with stonecutter, but they don't need stonecutter so we just disable
// it for all source sets that are not the main source set or the mixins source set
fun rfgStonecutterCompat(
    target: Project,
) {
    @Suppress("UnstableApiUsage")
    val mixinSourceSets =
        target.extensions.getByType<ModstitchExtension>().mixin.mixinSourceSets.map { it.sourceSetName.get() }
    val whitelistedSourceSets = listOf(
        SourceSet.MAIN_SOURCE_SET_NAME,
        SourceSet.TEST_SOURCE_SET_NAME,
    ) + mixinSourceSets

    val sourceSets = target.extensions.getByType<SourceSetContainer>()
    sourceSets.configureEach {
        if (name !in whitelistedSourceSets) {
            val capitalized = name.replaceFirstChar(Char::uppercaseChar)
            val toDisable = listOf(
                "stonecutterPrepare$capitalized",
                "stonecutterMerge$capitalized",
                "stonecutterGenerate$capitalized",
                this.getCompileTaskName("kotlin")
            )

            toDisable.forEach { task ->
                target.tasks.named(task).configure {
                    enabled = false
                }
            }
        }
    }
}

//todo hack https://github.com/GTNewHorizons/RetroFuturaGradle/issues/94
// Just a hack, there's a ConcurrentModificationException sometimes with accessing a hashmap
// so we use reflections to replace it with a ConcurrentHashMap
private fun reflectionsRFGFix() {

    val cl = Thread.currentThread().contextClassLoader
    val hashUtilsClass = Class.forName(
        "com.gtnewhorizons.retrofuturagradle.util.HashUtils", true,
        cl
    )

    val field = hashUtilsClass.getDeclaredField("fileHashCache").apply {
        isAccessible = true
    }
    val newCache: ConcurrentMap<File, Any> = ConcurrentHashMap()

    field.set(null, newCache)

    val patched = field.get(null)
    check(patched is ConcurrentHashMap<*, *>) {
        "RFG reflection patch failed: fileHashCache is ${patched?.javaClass?.name} " +
                "(HashUtils classloader=${hashUtilsClass.classLoader}, contextClassLoader=$cl)"
    }
}

