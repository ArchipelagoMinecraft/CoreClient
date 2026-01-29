package io.github.archipelagominecraft.plugin.configs

import dev.isxander.modstitch.base.BasePlugin
import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.isxander.modstitch.util.PlatformExtensionInfo
import io.archipelagominecraft.gradle.modInfo
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


// https://github.com/CleanroomMC/TemplateDevEnvKt/blob/master/build.gradle.kts
fun retroFuturaGradleConfiguration(
    target: Project,
){

    reflectionsRFGFix()
    // add RFG to modstitch
    addRfgModstitch(target)

    val mixinSourceSets = target.extensions.getByType<ModstitchExtension>().mixin.mixinSourceSets.map { it.sourceSetName.get() }
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


fun addRfgModstitch(target: Project) {
    val platformPlugin = ModstitchBaseCommonImplRFG()
    val unselectedPlatforms = BasePlugin.platforms.values
//    target.platform = selectedPlatform

    // apply the real plugin for the correct platform
    platformPlugin.apply(target)

    fun <T : Any> createDummyExtension(target: Project, extension: PlatformExtensionInfo<T>) {
        // multiple platforms may use the same extension, so only create a dummy if it doesn't already exist
        // the real platform is always applied first
        val alreadyExists = target.extensions.extensionsSchema
            .find { it.name == extension.name } != null

        if (!alreadyExists) {
            target.extensions.create(extension.api, extension.name, extension.dummyImpl)
        }
    }

    // create dud extensions for all other platforms
    // to generate type safety so even when the platform is not applied, the script can be compiled
    unselectedPlatforms.forEach { unselectedPlatform ->
        unselectedPlatform.platformExtensionInfo?.let {
            createDummyExtension(target, it)
        }
    }


}

//todo hack https://github.com/GTNewHorizons/RetroFuturaGradle/issues/94
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


