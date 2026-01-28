package io.github.archipelagominecraft.plugin.configs

import Keys
import com.gtnewhorizons.retrofuturagradle.MinecraftExtension
import com.gtnewhorizons.retrofuturagradle.UserDevPlugin
import com.gtnewhorizons.retrofuturagradle.mcp.DeobfuscateTask
import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import com.gtnewhorizons.retrofuturagradle.mcp.SharedMCPTasks
import com.gtnewhorizons.retrofuturagradle.minecraft.RunMinecraftTask
import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import com.gtnewhorizons.retrofuturagradle.util.Distribution
import com.gtnewhorizons.retrofuturagradle.util.HashUtils
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.replacementProperties
import io.archipelagominecraft.gradle.requiredProp
import io.github.archipelagominecraft.plugin.BuildMultiversionExtension
import io.github.archipelagominecraft.plugin.RunConfigurationData
import io.github.archipelagominecraft.plugin.Side
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar


// https://github.com/CleanroomMC/TemplateDevEnvKt/blob/master/build.gradle.kts
fun retroFuturaGradleConfiguration(
    target: Project,
    javaVersion: Provider<Int>,
    extension: BuildMultiversionExtension,
    runs: NamedDomainObjectSet<RunConfigurationData>,
): SpecificPluginApplicationResult {

    val propMappingsChannel = target.requiredProp(Keys.mcpMappingsChannel)
    val propMappingsVersion = target.requiredProp(Keys.mcpMappingsVersion)
    val modInfo = target.modInfo
    val use_mixins = target.findProperty("enable_mixin")?.toString()?.toBoolean() == true &&
        !modInfo.mixinsFilePrefix.isNullOrBlank()
    val use_coremod = false
    val coremod_plugin_class_name = ""
    val use_access_transformer = false
    val include_mod = false
    val archives_base_name = modInfo.mixinsFilePrefix

    reflectionsRFGFix()
    target.pluginManager.apply(UserDevPlugin::class.java)

    @Suppress("UnstableApiUsage")
    target.extensions.configure<JavaPluginExtension>() {
        toolchain {
            languageVersion.set(javaVersion.map { JavaLanguageVersion.of(it) })
            vendor.set(JvmVendorSpec.AZUL)
        }
        withSourcesJar()
    }
    target.tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    if(use_mixins){
        target.repositories {
            maven {
                name = "SpongePowered Maven"
                url = target.uri("https://repo.spongepowered.org/maven")
            }
        }
    }


    target.dependencies {
        val implementation = target.configurations.named("implementation")
        val api = target.configurations.named("api")
        val annotationProcessor = target.configurations.named("annotationProcessor")
        if (use_mixins) {
            implementation("zone.rong:mixinbooter:7.1")
        }

        // Example of deobfuscating a dependency
        // implementation rfg.deobf("curse.maven:had-enough-items-557549:4543375")

        if (use_mixins) {
            // Change your mixin refmap name here:
            val modUtils = target.extensions.getByType<ModUtils>()
            val mixin =
                modUtils.enableMixins(
                    "org.spongepowered:mixin:0.8.3",
                    "mixins.${archives_base_name}.refmap.json"
                ) as String
            api(mixin) {
                isTransitive = true
            }
            annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
            annotationProcessor("com.google.guava:guava:24.1.1-jre")
            annotationProcessor("com.google.code.gson:gson:2.8.6")
            annotationProcessor(mixin) {
                isTransitive = false
            }
        }
    }


    //In case of errors like "Cannot find Hunk target", try to clear your retrofuturagradle cache at
    // $GRADLE_USER_HOME
    target.extensions.configure<MinecraftExtension>() {
        mcVersion.set(modInfo.minecraftVersion)

        // MCP Mappings
        mcpMappingChannel.set(propMappingsChannel)
        mcpMappingVersion.set(propMappingsVersion)

        // Add any additional tweaker classes here
        // extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")

        // Add various JVM arguments here for runtime
        val args = mutableListOf("-ea:${target.group}")
        if (use_coremod) {
            args += "-Dfml.coreMods.load=$coremod_plugin_class_name"
        }
        if (use_mixins) {
            args += "-Dmixin.hotSwap=true"
            args += "-Dmixin.checks.interfaces=true"
            args += "-Dmixin.debug.export=true"
        }
        extraRunJvmArguments.addAll(args)

        // This causes the project to compile when syncing, (and this option shouldn't be useful for us anyway)
        // Include and use dependencies' Access Transformer files
//        useDependencyAccessTransformers.set(true)
    }
    configureRuns(runs, target)
    if (extension.enableJvmDowngrader.get()) {
        configureDowngradedRuns(target)
    }


// Adds Access Transformer files to tasks
    if (use_access_transformer) {
        val archivesBaseName =
            archives_base_name ?: error("archives_base_name required for use_access_transformer=true")
        setupAccessTransformers(target, archivesBaseName)
    }

    setupResourcesProcessing(target, use_access_transformer)

    target.tasks.withType<Jar> {
        manifest {
            val attributeMap = mutableMapOf<String, String>()
            if (use_coremod) {
                attributeMap["FMLCorePlugin"] = coremod_plugin_class_name
                if (include_mod) {
                    attributeMap["FMLCorePluginContainsFMLMod"] = true.toString()
                    attributeMap["ForceLoadAsMod"] = (project.gradle.startParameter.taskNames[0] == "build").toString()
                }
            }
            attributes(attributeMap)
        }
    }


    //stonecutter ordering fixes
    val rfgSourceSets = listOf(
        SharedMCPTasks.SOURCE_SET_LAUNCHER,
        "injectedInterfaces",
        "injectedTags",
        SharedMCPTasks.SOURCE_SET_PATCHED_MC
    )

    val sourceSets = target.extensions.getByType<SourceSetContainer>()
    rfgSourceSets.forEach {
        sourceSets.named(it) {
            target.tasks.named(getCompileTaskName("kotlin")) {
                enabled = false
            }
        }
//        sourceSets.named(it) {
//            val sourceSetName = this.name
//            val compileTasks = listOf(compileJavaTaskName, processResourcesTaskName)
//            compileTasks.forEach {
//                target.tasks.findByName(it)?.apply {
//                    dependsOn("stonecutterMerge${sourceSetName.replaceFirstChar(Char::uppercase)}")
//                }
//            }
//        }
//        val name = it.replaceFirstChar(Char::uppercase)
//        val generate = "stonecutterGenerate$name"
//        target.tasks.named(generate).configure {
//            dependsOn("stonecutterMerge$name")
//        }
    }




    return SpecificPluginApplicationResult(target.configurations.named("implementation")) //todo check
}

private fun configureRuns(
    runs: NamedDomainObjectSet<RunConfigurationData>,
    target: Project,
) {
    runs.all {
        val data = this
        val dist = when (data.side.get()) {
            Side.Client -> Distribution.CLIENT
            Side.Server -> Distribution.DEDICATED_SERVER
        }
        target.tasks.maybeRegister<RunMinecraftTask>("run${name.replaceFirstChar(Char::uppercaseChar)}", dist) {
            workingDir = data.workingDirectory.get().asFile
            extraArgs.set(data.args)
        }
    }
}

private fun configureDowngradedRuns(target: Project) {
    val downgradeJar = target.tasks.named("downgradeJar", DowngradeJar::class.java)

    target.tasks.withType<ReobfuscatedJar>().matching { it.name == "reobfJar" }.configureEach {
        inputJar.set(downgradeJar.flatMap { it.archiveFile })
    }

    target.tasks.withType<RunMinecraftTask>().configureEach {
        val shadeDowngradedApi = target.tasks.named("shadeDowngradedApi", ShadeJar::class.java)
        val jarTask = target.tasks.named("jar", Jar::class.java)
        dependsOn(shadeDowngradedApi)
        doFirst {
            val downgraded = shadeDowngradedApi.get().archiveFile.get().asFile
            require(downgraded.exists()) { "Downgraded jar does not exist: ${downgraded.absolutePath}" }
            val originalClasspath = classpath.minus(jarTask.get().outputs.files)
            classpath = target.files(downgraded).plus(originalClasspath)
        }
    }
}

private fun setupAccessTransformers(target: Project, archivesBaseName: String) {
    for (at in target.extensions.getByType<SourceSetContainer>().getByName("main").resources.files) {
        if (at.name.lowercase().endsWith("_at.cfg")) {
            target.tasks.withType<DeobfuscateTask>().named("deobfuscateMergedJarToSrg")
                .get().accessTransformerFiles.from(at)
            target.tasks.withType<DeobfuscateTask>().named("srgifyBinpatchedJar").get().accessTransformerFiles.from(
                at
            )
        }
    }
    target.tasks.withType<Jar> {
        manifest {
            attributes.set(
                "FMLAT",
                archivesBaseName + "_at.cfg"
            )
        }
    }
}

private fun setupResourcesProcessing(
    target: Project,
    moveAccessTransformerFile: Boolean,
): SourceSetContainer {
    val sourceSets = target.extensions.getByType<SourceSetContainer>()
    val resourcesTargetDir = target.layout.buildDirectory.dir("generated/rfgResources")
    sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME) {
        resources.srcDir(resourcesTargetDir)
    }
    val properties = target.replacementProperties
    @Suppress("UnstableApiUsage")
    target.tasks.withType<ProcessResources> {
        from("templates") //modstitch folder name
        into(resourcesTargetDir)
        // This will ensure that this task is redone when the versions change

        // Replace various properties in mcmod.info and pack.mcmeta if applicable
        filesMatching(arrayListOf("mcmod.info", "pack.mcmeta", "**/*.mixins.json")) {
            expand(
                properties
            )
        }

        if (moveAccessTransformerFile) {
            rename("(.+_at.cfg)", "META-INF/$1") // Make sure Access Transformer files are in META-INF folder
        }
    }
    return sourceSets
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
    // Use correct key type (File) to match the library's field.
    val newCache: ConcurrentMap<File, Any> = ConcurrentHashMap()

    field.set(null, newCache)

    // Verify immediately; if this fails, you are patching the wrong classloader/copy.
    val patched = field.get(null)
    check(patched is ConcurrentHashMap<*, *>) {
        "RFG reflection patch failed: fileHashCache is ${patched?.javaClass?.name} " +
                "(HashUtils classloader=${hashUtilsClass.classLoader}, contextClassLoader=$cl)"
    }
}


inline fun <reified T : Task> TaskContainer.maybeRegister(
    name: String,
    vararg constructorArgs: Any,
    configure: Action<T>,
) {
    if (name in names) {
        this.withType<T>().named(name, configure)
    } else {
        this.register(name, T::class.java, configure, constructorArgs)
    }
}
