package io.github.archipelagominecraft.plugin.configs

import Keys
import com.gtnewhorizons.gtnhgradle.GTNHGradlePlugin
import com.gtnewhorizons.gtnhgradle.GTNHModule
import com.gtnewhorizons.gtnhgradle.modules.*
import com.gtnewhorizons.retrofuturagradle.mcp.SharedMCPTasks
import gradle.kotlin.dsl.accessors._1ffcea057cc3dd5dcbcecb169c9d0988.sourceSets
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.requiredProp
import io.github.archipelagominecraft.plugin.BuildMultiversionExtension
import io.github.archipelagominecraft.plugin.RunConfigurationData
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra


fun gtnhGradleConfiguration(
    target: Project,
    extension: BuildMultiversionExtension,
    runs: NamedDomainObjectSet<RunConfigurationData>,
): SpecificPluginApplicationResult {
    val modulesToDisable = listOf(
        GitVersionModule::class.java,
        CodeStyleModule::class.java,
        PublishingModule::class.java, //todo check
        UpdaterModule::class.java,
        IdeIntegrationModule::class.java, // doesn't work because of the idea plugin
    )


    val propMappingsChannel = target.requiredProp(Keys.mcpMappingsChannel)
    val propMappingsVersion = target.requiredProp(Keys.mcpMappingsVersion)
    val modInfo = target.modInfo
    val props = mapOf(
        "modId" to modInfo.id,
        "modName" to modInfo.name,
        "modVersion" to modInfo.version,
        "modGroup" to modInfo.packageName,
        "minecraftVersion" to modInfo.minecraftVersion,
        "forgeVersion" to target.requiredProp(Keys.forgeVersion),
        "channel" to propMappingsChannel,
        "mappingsVersion" to propMappingsVersion,
        "enableModernJavaSyntax" to extension.enableJvmDowngrader.map { if(it) "jvmDowngrader" else "false" }.get(),
        "jvmDowngraderStubsProvider" to "shade",
        "enableGenericInjection" to "true",
        "usesMixins" to if (extension.mixinsPackage.isPresent) "true" else "false",
        "mixinsPackage" to extension.mixinsPackage.orElse("")
    )
    props.forEach { (key, value) -> target.extra.set(key, value) }

    //Create empty directories because GTNHGradle checks for their existence
    // Normally the check would work but because we are using stonecutter here, there are no source directories
    // in the versioned projects (1.12.2-forge, etc)
    target.file("src/main/java/" + modInfo.packageName.replace('.', '/')).mkdirs()

    target.pluginManager.apply(GTNHGradlePlugin::class.java)

    target.extensions.configure<GTNHGradlePlugin.GTNHExtension> {
        GTNHGradlePlugin.GTNHExtension.ALL_MODULES.filter { it !in modulesToDisable }.forEach {
            GTNHModule.applyIfEnabled(it, this, target)
        }
    }

    listOf(
        SharedMCPTasks.SOURCE_SET_LAUNCHER,
        "injectedInterfaces",
        SharedMCPTasks.SOURCE_SET_PATCHED_MC
    ).forEach {
        target.sourceSets.named(it){
            val sourceSetName = this.name
            val compileTasks = listOf(compileJavaTaskName,processResourcesTaskName,getCompileTaskName("kotlin"))
            compileTasks.forEach {
                target.tasks.findByName(it)?.apply {
                    dependsOn("stonecutterMerge${sourceSetName.replaceFirstChar(Char::uppercase)}")
                }
            }
        }
        val name = it.replaceFirstChar(Char::uppercase)
        val generate = "stonecutterGenerate$name"
        target.tasks.named(generate).configure {
            dependsOn("stonecutterMerge$name")
        }
    }



    return SpecificPluginApplicationResult(target.configurations.named("implementation")) //todo check
}
