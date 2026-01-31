package io.github.archipelagominecraft.buildplugin.configs

import dev.kikugie.fletching_table.FletchingTablePlugin
import dev.kikugie.fletching_table.extension.FletchingTableExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.github.archipelagominecraft.buildplugin.ModLoaders
import io.github.archipelagominecraft.buildplugin.loader
import io.github.archipelagominecraft.buildplugin.modInfo
import org.cthing.gradle.plugins.buildconstants.BuildConstantsTask
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension


fun commonConfiguration(target: Project) {

    val loader = target.loader
    configureStonecutter(target, loader)
    configureFletchingTable(target)
    configureBuildConstants(target)

}

// generates a class with theses constants inside
private fun configureBuildConstants(target: Project) {
    target.pluginManager.apply("org.cthing.build-constants")
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

// Specify stonecutterr constands and which resources files it should process
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

private fun configureFletchingTable(target: Project) {
    target.pluginManager.apply(FletchingTablePlugin::class.java)
    target.extensions.configure<FletchingTableExtension>{
        j52j.register(SourceSet.MAIN_SOURCE_SET_NAME){
            extension("json","*.mixins.json5")
            extension("info","mcmod.json5")
        }
    }
}
