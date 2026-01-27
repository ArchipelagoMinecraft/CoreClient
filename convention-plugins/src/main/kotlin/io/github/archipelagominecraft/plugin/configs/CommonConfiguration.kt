package io.github.archipelagominecraft.plugin.configs

import ModLoaders
import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.isxander.modstitch.util.Side
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo
import org.cthing.gradle.plugins.buildconstants.BuildConstantsTask
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension


fun commonConfiguration(target: Project) {
    target.pluginManager.apply {
        apply("org.cthing.build-constants")
    }
    val loader = target.loader
    configureStonecutter(target, loader)

    //todo use gradle providers for loader
    val modInfo = target.modInfo
    val generateBuildConstants = target.tasks.named<BuildConstantsTask>("generateBuildConstants") {
        classname.set(modInfo.packageName + "." + modInfo.name + "Constants")
        additionalConstants.put("MOD_ID", modInfo.id)
        additionalConstants.put("MOD_NAME", modInfo.name)
        additionalConstants.put("MOD_VERSION", modInfo.version)
        additionalConstants.put("MOD_MIXINS_FILE_PREFIX", modInfo.mixinsFilePrefix ?: "")
    }
    target.extensions.configure<KotlinBaseExtension>() {
        sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            .kotlin
            .srcDirs(generateBuildConstants)
    }
}

private fun configureStonecutter(target: Project, loader: ModLoaders) {
    target.extensions.configure<StonecutterBuildExtension> {
        filters.include(
            "resources/**.json",
            "templates/**.mcmeta",
            "templates/**.json",
            "templates/**.info"
        )
        constants["fabric"] = loader == ModLoaders.FABRIC
        constants["neoforge"] = loader == ModLoaders.NEOFORGE
        constants["forge"] = loader == ModLoaders.FORGE
        constants["forgeLike"] = loader == ModLoaders.FORGE || loader == ModLoaders.NEOFORGE
    }
}
