package io.github.archipelagominecraft.plugin.configs

import com.gtnewhorizons.retrofuturagradle.MinecraftExtension
import com.gtnewhorizons.retrofuturagradle.UserDevPlugin
import com.gtnewhorizons.retrofuturagradle.mcp.JSTTransformerTask
import com.gtnewhorizons.retrofuturagradle.mcp.MCPTasks
import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import com.gtnewhorizons.retrofuturagradle.minecraft.RunMinecraftTask
import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import com.gtnewhorizons.retrofuturagradle.util.Distribution
import dev.isxander.modstitch.base.AppendModMetadataTask
import dev.isxander.modstitch.base.BaseCommonImpl
import dev.isxander.modstitch.base.FutureNamedDomainObjectProvider
import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.isxander.modstitch.base.moddevgradle.GenerateAccessTransformerTask
import dev.isxander.modstitch.util.*
import net.neoforged.srgutils.IMappingFile
import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import xyz.wagyourtail.jvmdg.gradle.JVMDowngraderPlugin
import xyz.wagyourtail.jvmdg.gradle.jvmdg
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.io.File
import javax.inject.Inject


fun Project.retroFuturaGradle(action: Action<BaseRFGExtension>) =
    this.extensions.findByType<BaseRFGExtension>()?.let { action(it) }

class ModstitchBaseCommonImplRFG : BaseCommonImpl<BaseRFGExtension>(
    Platform.MDG,
    AppendRFGMetadataTask::class.java
) {

    val nonDowngradedRegularConfigurations = mutableSetOf<String>()

    override val platformExtensionInfo: PlatformExtensionInfo<BaseRFGExtension> = PlatformExtensionInfo(
        "msRetroFuturaGradle",
        BaseRFGExtension::class,
        BaseRFGExtensionImpl::class,
        BaseRFGExtensionDummy::class
    )

    override fun applyPlugins(target: Project) {
        super.applyPlugins(target)
        // https://github.com/GTNewHorizons/RetroFuturaGradle/issues/38
        target.extraProperties["rfg.disableDependencyDeobfuscation"] = "true"
        target.pluginManager.apply(UserDevPlugin::class.java)
        target.pluginManager.apply(JVMDowngraderPlugin::class.java)
    }

    override fun applyDefaultRepositories(repositories: RepositoryHandler) {
        super.applyDefaultRepositories(repositories)

        repositories.maven("https://jitpack.io") {
            name = "JitPack UniMixins"
            mavenContent {
                includeGroup("com.github.LegacyModdingMC.UniMixins")
            }
        }
        repositories.maven("https://maven.cleanroommc.com") {
            name = "MixinBooter"
            mavenContent {
                includeModule("zone.rong", "mixinbooter")
            }
        }
    }

    override fun applyJavaSettings(target: Project) {
        super.applyJavaSettings(target)
        val ext = target.extensions.getByType<BaseRFGExtension>()
        val modstitch = target.extensions.getByType<ModstitchExtension>()
        target.extensions.configure<JavaPluginExtension> {
            target.afterSuccessfulEvaluate {
                val javaVer = ext.developmentJavaVersion.orNull
                ext.enableJvmDowngrader.finalizeValueOnRead()
                if (ext.enableJvmDowngrader.get() && javaVer != null) {
                    JavaVersion.toVersion(javaVer).let {
                        sourceCompatibility = it
                        targetCompatibility = it
                    }
                }
            }
            toolchain {
                languageVersion.set(
                    ext.developmentJavaVersion.orElse(modstitch.javaVersion).map { JavaLanguageVersion.of(it) }
                )
                // https://github.com/MinecraftForge/ForgeGradle/issues/597
                // Important for stable decompilation output or else you get failed
                // patches with: cannot find hunk target
                vendor.set(JvmVendorSpec.AZUL)
            }
        }
    }

    override fun apply(target: Project) {
        val ext = createRealPlatformExtension(target)!!
        super.apply(target)
        val modstitch = target.extensions.getByType<ModstitchExtension>()
        //todo set final jar tasks modstitch api
        target.tasks.named("reobfJar", ReobfuscatedJar::class.java) {
            if (ext.enableJvmDowngrader.get()) {
                val oldJar = inputJar.get()
                inputJar.set(target.jvmdg.defaultTask.flatMap { it.archiveFile }.orElse(oldJar))
            }
        }
        target.tasks.named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME) {
            if (ext.enableJvmDowngrader.get()) {
                dependsOn(target.jvmdg.defaultShadeTask)
            }
        }

        modstitch.modLoaderManifest.convention("mcmod.info")
        val minecraft = target.extensions.getByType<MinecraftExtension>()
        minecraft.mcVersion.set(modstitch.minecraftVersion)
        minecraft.mcpMappingChannel.set(ext.mcpChannel)
        minecraft.mcpMappingVersion.set(ext.mcpVersion)
        minecraft.usesForge.set(ext.usesForge)
        minecraft.usesFml.set(ext.usesFML)
        minecraft.extraRunJvmArguments.addAll(
            mutableListOf(
                "-ea:${target.group}",
                "-Dmixin.hotSwap=true",
                "-Dmixin.check.interfaces=true",
                "-Dmixin.debug.export=true"
            )
        )
        minecraft.extraRunJvmArguments.addAll(
            ext.coreModClassName.map {
                listOf("-Dfml.coreMods.load=$it")
            }.orElse(emptyList())
        )

        configureLegacyMixin(target)

        applyRuns(target)
        fixJvmDowngraderRuns(target)
    }

    override fun applyClassTweaker(target: Project) {
        val modstitch = target.extensions.getByType<ModstitchExtension>()
        val defaultAccessTransformerName = modstitch.metadata.modId.map { "META-INF/${it}_at.cfg" }
        val generatedAccessTransformer = defaultAccessTransformerName.flatMap {
            target.layout.buildDirectory
                .file("modstitch/$it")
        }.zip(modstitch.classTweaker) { x, _ -> x }
        val generatedAccessTransformersList = generatedAccessTransformer.map { listOf(it) }.orElse(listOf())
        val classTweakerName = modstitch.classTweakerName.convention(defaultAccessTransformerName)
            .map {
                if (!it.startsWith("META-INF")) {
                    error("Access transformer name must be placed in META-INF/")
                } else it
            }
        val classTweakerPath = classTweakerName.map { it.split('\\', '/') }
        modstitch.classTweaker.finalizeValueOnRead()
        modstitch.classTweakerName.finalizeValueOnRead()
        modstitch.validateClassTweaker.finalizeValueOnRead()


        val mcpTasks = target.extensions.getByType<MCPTasks>()

        val convertMappingsTask = target.tasks.register<ConvertMappingsTask>("convertMappingsForAccessTransformers") {
            sourceMappingsFile.set(mcpTasks.taskGenerateForgeSrgMappings.flatMap { it.mcpToSrg })
            targetFormat.set(IMappingFile.Format.TSRG)
            convertedFile.set(target.layout.buildDirectory.file("generated/convertedMappings.txt"))
        }

        val generateAccessTransformerTask =
            target.tasks.register<GenerateAccessTransformerTask>("generateAccessTransformer") {
                group = "modstitch/internal"
                description = "Generates an access transformer."
                classTweaker.set(modstitch.classTweaker)
                mappings.set(
                    convertMappingsTask.flatMap { it.convertedFile }
                )
                accessTransformer.set(generatedAccessTransformer)
            }

//        val projectDir = target.projectDir.toPath()
//        mcpTasks.deobfuscationATs.from(
//            generateAccessTransformerTask.map {
//                it.outputs.files.map {
//                    projectDir.relativize(it.toPath())
//                }
//            }
//        )
        target.tasks.named<JSTTransformerTask>("applyJST") {
            this.accessTransformerFiles.setFrom(generateAccessTransformerTask)
        }


        target.tasks.named<ProcessResources>("processResources") {
            dependsOn(generateAccessTransformerTask)
            from(generatedAccessTransformersList) {
                rename { classTweakerPath.get().last() }
                into(classTweakerPath.map { it.dropLast(1).joinToString("/") })
            }
        }
        target.tasks.named<Jar>("jar") {
            manifest {
                attributes["FMLAT"] = classTweakerPath.get().last()
            }
        }

    }


    private fun fixJvmDowngraderRuns(target: Project) {
        val ext = target.extensions.getByType<BaseRFGExtension>()
        val defaultShadeTask = target.jvmdg.defaultShadeTask
        val nonDowngradedJar = target.tasks.named("jar", Jar::class.java).map { it.outputs.files }
        val downgradedJarOutput = defaultShadeTask.map { it.outputs.files }
        val nonDowngradedDependencies = target.provider {
            target.files(
                nonDowngradedRegularConfigurations
                    .flatMap {
                        val config = target.configurations.getByName(it)
                        if (config.isCanBeResolved) config.resolve() else emptySet()
                    }
            )
        }
        target.tasks.withType<RunMinecraftTask>().configureEach {
            if (ext.enableJvmDowngrader.get()) {
                dependsOn(defaultShadeTask)
                classpath(downgradedJarOutput.get())
                val originalClasspath = classpath
                //downgraded jar in first position

                classpath = downgradedJarOutput.get()
                    .plus(
                        originalClasspath
                            .minus(nonDowngradedJar.get())
                            .minus(nonDowngradedDependencies.get())
                    )
            }
        }
    }


    private fun applyRuns(target: Project) {
        val modstitch = target.extensions.getByType<ModstitchExtension>()
        val ext = target.extensions.getByType<BaseRFGExtension>()


        modstitch.runs.whenObjectAdded {
            val config = this
            val taskName = "run${config.name.replaceFirstChar(Char::uppercaseChar)}"

            modstitch.onEnable {
                config.side.finalizeValueOnRead()
                target.tasks.maybeRegister<RunMinecraftTask>(
                    taskName,
                    config.side.map {
                        when (it) {
                            Side.Both -> error("Unknown side for RetroFuturaGradle: $it")
                            Side.Client -> Distribution.CLIENT
                            Side.Server -> Distribution.DEDICATED_SERVER
                        }
                    }
                ) {
                    group = "modstitch/runs"
                    config.gameDirectory.finalizeValueOnRead()
                    config.gameDirectory.orNull?.let {
                        this.workingDir = it.asFile
                    }
                    this.mainClass.set(config.mainClass)
                    this.jvmArguments.set(config.jvmArgs)
                    this.extraArgs.set(config.programArgs)
                    config.environmentVariables.finalizeValueOnRead()
                    config.environmentVariables.orNull?.let { this.environment = it }

                }
                val bool = target.gradle.startParameter.taskNames[0] == "build"
                target.tasks.named<Jar>("jar") {
                    manifest {
                        val attributeMap = mutableMapOf<String, String>()
                        val coreModClassName = ext.coreModClassName.orNull
                        if (coreModClassName != null) {
                            attributeMap["FMLCorePlugin"] = coreModClassName
                            if (ext.hasModAndCoreMod.get()) {
                                attributeMap["FMLCorePluginContainsFMLMod"] = true.toString()
                                attributeMap["ForceLoadAsMod"] = bool.toString()
                            }
                            attributes.putAll(attributeMap)
                        }
                    }
                }
            }
        }

    }

    override fun finalize(target: Project) {
        val ext = target.extensions.getByType<BaseRFGExtension>()
        val minecraft = target.extensions.getByType<MinecraftExtension>()
        check(minecraft.forgeVersion.get() == ext.forgeVersion.get()) {
            "Unsupported forge version ${ext.forgeVersion.get()} for RetroFuturaGradle, expected ${minecraft.forgeVersion.get()} " +
                    "RFG only supports 1 version for 1.7.10 and another one for 1.12.2"
        }
        super.finalize(target)
    }

    override fun applyMetadataStringReplacements(target: Project): TaskProvider<ProcessResources> {
        val generateModMetadata = super.applyMetadataStringReplacements(target)

        return generateModMetadata
    }

    override fun applyUnitTesting(target: Project, testFrameworkConfigure: Action<in JUnitPlatformOptions>) {
        throw UnsupportedOperationException("RetroFuturaGradle does not support unit testing")
    }

    override fun createProxyConfigurations(
        target: Project,
        configuration: FutureNamedDomainObjectProvider<Configuration>,
        defer: Boolean,
    ) {
        val ext = target.extensions.getByType<BaseRFGExtension>()
        val modstitch = target.extensions.getByType<ModstitchExtension>()
        val proxyModConfigurationName = configuration.name.addCamelCasePrefix("modstitchMod")
        val proxyRegularConfigurationName = configuration.name.addCamelCasePrefix("modstitch")
        val proxyDowngradeConfigurationName = configuration.name.addCamelCasePrefix("modstitchDowngrade")

        // already created
        if (target.configurations.find { it.name == proxyModConfigurationName } != null) {
            return
        }
        fun deferred(action: (Configuration) -> Unit) {
            if (!defer) return action(configuration.get())
            return target.afterSuccessfulEvaluate { action(configuration.get()) }
        }

        val proxyMod = target.configurations.create(proxyModConfigurationName) proxy@{
            deferred {
                it.extendsFrom(this@proxy)
            }
        }

        val rfg = target.dependencies.extensions.getByType<ModUtils.RfgDependencyExtension>()
        proxyMod.dependencies.configureEach {
            if (this is FileCollectionDependency) {
                rfg.deobf(this.files)
            } else {
                rfg.deobf(
                    mapOf(
                        "group" to this.group.orEmpty(),
                        "name" to this.name.orEmpty(),
                        "version" to this.version.orEmpty(),
                        //todo: RFG also checks the classifier but IDK how to get it from here
                    )
                )
            }
        }

        deferred {
            nonDowngradedRegularConfigurations.add(it.name)
        }
        nonDowngradedRegularConfigurations.add(proxyRegularConfigurationName)
        target.configurations.create(proxyRegularConfigurationName) proxy@{
            deferred {
                it.extendsFrom(this@proxy)
            }
        }
        val javaVersion = modstitch.javaVersion.map {
            JavaVersion.toVersion(it)
        }
        val downgrade = target.configurations.create(proxyDowngradeConfigurationName) proxy@{
            deferred {
                it.extendsFrom(this@proxy)
            }
        }
        target.afterSuccessfulEvaluate {
            if (ext.enableJvmDowngrader.get()) {
                target.jvmdg.dg(downgrade, false) {
                    downgradeTo.set(javaVersion)
                }
            }
        }
    }

    override fun configureJiJConfiguration(target: Project, configuration: Configuration) {
        target.afterSuccessfulEvaluate {
            configuration.dependencies.whenObjectAdded {
                error("RetroFuturaGradle does not support JarInJar, please use the 'modstitch' configuration instead'")
            }
        }
    }


    @Suppress("UnstableApiUsage")
    private fun configureLegacyMixin(target: Project) {
        val modstitch = target.extensions.getByType<ModstitchExtension>()
        val ext = target.extensions.getByType<BaseRFGExtension>()
        val stitchedMixin = modstitch.mixin
        ext.mixinsDependencies.finalizeValueOnRead()

        addMixinDependencies(target, ext.mixinsDependencies)
        val modUtils = target.extensions.getByType<ModUtils>()
        modUtils.enableMixins(
            null,
            modstitch.metadata.modId.map { "$it.refmap.json" }.get()
        )

        stitchedMixin.mixinSourceSets.whenObjectAdded obj@{
            modUtils.mixinSourceSet.set(target.sourceSets[this.sourceSetName.get()])
        }
        target.afterEvaluate {
            if (stitchedMixin.mixinSourceSets.size > 1) {
                //technically it does but we'll see later
                error("RetroFuturaGradle does not support multiple mixin source sets")
            }
            if (stitchedMixin.configs.size > 1) {
                error("RetroFuturaGradle does not support multiple mixin configs")
            }
        }
    }

    override fun onEnable(target: Project, action: Action<Project>) {
        target.afterSuccessfulEvaluate(action)
    }
}

@CacheableTask
abstract class ConvertMappingsTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceMappingsFile: RegularFileProperty

    @get:Input
    abstract val targetFormat: Property<IMappingFile.Format>

    @get:OutputFile
    abstract val convertedFile: RegularFileProperty

    @TaskAction
    fun convert() {
        val loadedMappings = IMappingFile.load(this.sourceMappingsFile.get().asFile)
        loadedMappings.write(convertedFile.get().asFile.toPath(), targetFormat.get(), false)
    }

}


private fun addMixinDependencies(
    target: Project,
    dependencies: Provider<List<String>>,
) {
    target.afterSuccessfulEvaluate {
        target.dependencies {
            val implementation = target.configurations.named(JvmConstants.IMPLEMENTATION_CONFIGURATION_NAME)
            val annotationProcessor =
                target.configurations.getByName(JvmConstants.ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
            dependencies.get().forEach {
                implementation(it)
                annotationProcessor(it)
            }
        }
    }
}

//modstitch internal
private fun Project.afterSuccessfulEvaluate(action: Action<Project>) = project.afterEvaluate {
    if (state.failure == null) {
        action.execute(this)
    }
}

private fun String.addCamelCasePrefix(prefix: String): String =
    replaceFirstChar { prefix + it.uppercaseChar() }


interface BaseRFGExtension {
    val forgeVersion: Property<String>

    val usesFML: Property<Boolean>
    val usesForge: Property<Boolean>

    val mcpVersion: Property<String>

    val mcpChannel: Property<String>

    val enableJvmDowngrader: Property<Boolean>
    val developmentJavaVersion: Property<Int>

    // if you use enableJvmDowngrader you must shade this/provide this at runtime
    val jvmDowngraderApiDependency: Provider<List<File>>

    val jvmDowngraderShadeApiTask: TaskProvider<ShadeJar>
    val jvmDowngraderDowngradeJarTask: TaskProvider<DowngradeJar>

    val coreModClassName: Property<String>

    val hasModAndCoreMod: Property<Boolean>

    val mixinsDependencies: ListProperty<String>

    val rfgExtension: MinecraftExtension

    fun configureRFG(action: Action<MinecraftExtension>)

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

open class BaseRFGExtensionImpl @Inject constructor(
    objects: ObjectFactory,
    @Transient private val project: Project,
) : BaseRFGExtension {
    override val forgeVersion: Property<String> = objects.property()
    override val usesFML: Property<Boolean> = objects.property()
    override val usesForge: Property<Boolean> = objects.property()
    override val mcpVersion: Property<String> = objects.property()
    override val mcpChannel: Property<String> = objects.property()
    override val enableJvmDowngrader: Property<Boolean> = objects.property<Boolean>()
    override val jvmDowngraderApiDependency: Provider<List<File>>
        get() = project.jvmdg.apiJar
    override val developmentJavaVersion: Property<Int> = objects.property<Int>()
    override val coreModClassName: Property<String> = objects.property()
    override val hasModAndCoreMod: Property<Boolean> = objects.property<Boolean>()
    override val rfgExtension: MinecraftExtension
        get() = project.extensions.getByType<MinecraftExtension>()
    override val jvmDowngraderShadeApiTask: TaskProvider<ShadeJar>
        get() = project.jvmdg.defaultShadeTask
    override val mixinsDependencies: ListProperty<String> = objects.listProperty()
    override val jvmDowngraderDowngradeJarTask: TaskProvider<DowngradeJar>
        get() = project.jvmdg.defaultTask

    override fun configureRFG(action: Action<MinecraftExtension>) = action(rfgExtension)

    init {
        forgeVersion.finalizeValueOnRead()
        usesFML.finalizeValueOnRead()
        usesForge.finalizeValueOnRead()
        mcpVersion.finalizeValueOnRead()
        mcpChannel.finalizeValueOnRead()
        enableJvmDowngrader.finalizeValueOnRead()
        developmentJavaVersion.finalizeValueOnRead()
        coreModClassName.finalizeValueOnRead()
        hasModAndCoreMod.finalizeValueOnRead()
        mixinsDependencies.finalizeValueOnRead()
        val mixinBooterDeps = listOf(
            "zone.rong:mixinbooter:10.7",
            "org.ow2.asm:asm-debug-all:5.2"
        )
        val unimixinsMixin = "com.github.LegacyModdingMC.UniMixins:unimixins-all-1.7.10:0.2.1"
        val mixinDependency = project.provider {
            project.extensions.getByType<ModstitchExtension>()
        }.flatMap {
            it.minecraftVersion
        }.map {
            MinecraftVersion.parseOrderableOrNull(it)?.let {
                if (it == MinecraftVersion.LegacyRelease(8, 9)) {
                    mixinBooterDeps
                } else if (it == MinecraftVersion.LegacyRelease(12, 2)) {
                    mixinBooterDeps
                } else if (it == MinecraftVersion.LegacyRelease(7, 10)) {
                    listOf(unimixinsMixin)
                } else null
            }
        }
        mixinsDependencies.convention(mixinDependency)
    }
}

open class BaseRFGExtensionDummy : BaseRFGExtension {
    override val forgeVersion: Property<String> by NotExistsDelegate()
    override val usesFML: Property<Boolean> by NotExistsDelegate()
    override val usesForge: Property<Boolean> by NotExistsDelegate()
    override val mcpVersion: Property<String> by NotExistsDelegate()
    override val mcpChannel: Property<String> by NotExistsDelegate()
    override val enableJvmDowngrader: Property<Boolean> by NotExistsDelegate()
    override val developmentJavaVersion: Property<Int> by NotExistsDelegate()
    override val coreModClassName: Property<String> by NotExistsDelegate()
    override val hasModAndCoreMod: Property<Boolean> by NotExistsDelegate()
    override val jvmDowngraderApiDependency: Provider<List<File>> by NotExistsDelegate()
    override val rfgExtension: MinecraftExtension by NotExistsDelegate()
    override val mixinsDependencies: ListProperty<String> by NotExistsDelegate()
    override val jvmDowngraderShadeApiTask: TaskProvider<ShadeJar> by NotExistsDelegate()
    override val jvmDowngraderDowngradeJarTask: TaskProvider<DowngradeJar> by NotExistsDelegate()
    override fun configureRFG(action: Action<MinecraftExtension>) {}
}


abstract class AppendRFGMetadataTask : AppendModMetadataTask() {
    override fun appendModMetadata(file: File) {
        //nothing
    }
}
