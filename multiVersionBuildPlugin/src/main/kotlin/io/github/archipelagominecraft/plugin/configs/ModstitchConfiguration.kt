package io.github.archipelagominecraft.plugin.configs

import Keys
import ModLoaders
import dev.isxander.modstitch.base.AppendModMetadataTask
import dev.isxander.modstitch.base.extensions.ModstitchExtension
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.replacementProperties
import io.archipelagominecraft.gradle.requiredProp
import io.github.archipelagominecraft.plugin.RunConfigurationData
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.accessors.runtime.maybeRegister
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

fun modstitchConfiguration(
    target: Project,
    javaVersion: Provider<Int>,
    loader: Provider<ModLoaders>,
    runs: NamedDomainObjectSet<RunConfigurationData>,
): SpecificPluginApplicationResult {
    // We need to convert the loader into the correct modstitch platform property, **before** applying the
    // modstitch plugin
    target.project.extra["modstitch.platform"] = when (loader.get()) {
        ModLoaders.NEOFORGE -> "moddevgradle"
        ModLoaders.FORGE -> "moddevgradle-legacy"
        ModLoaders.FABRIC -> "fabric-loom-remap"
        ModLoaders.NONE_VANILLA -> "moddevgradle"
    }
    target.pluginManager.apply {
        apply("dev.isxander.modstitch.base")
    }
    val modstitchModImplementation = target.configurations.named("modstitchModImplementation")
    target.extensions.configure<KotlinJvmExtension>() {
        @Suppress("UnstableApiUsage")
        jvmToolchain {
            languageVersion.set(javaVersion.map { JavaLanguageVersion.of(it) })
        }
    }
    target.extensions.configure<ModstitchExtension>() {

        val modInfo = target.modInfo
        minecraftVersion.set(modInfo.minecraftVersion)
        this.javaVersion.set(javaVersion)

        // This metadata is used to fill out the information inside
        // the metadata files found in the templates folder.
        //todo do this also for RFG
        metadata {
            modId.set(modInfo.id)
            replacementProperties.putAll(target.replacementProperties)
        }

        parchment {
            mappingsVersion.set(target.requiredProp(Keys.parchmentMappingsVersion))
        }

        configureRuns(
            target,
            runs
        )


        // Fabric Loom (Fabric)
        loom {
            fabricLoaderVersion.set(target.requiredProp(Keys.fabricLoaderVersion))
        }

        // NeoForge
        moddevgradle {
            // If vanilla, we use neoform, else we use moddevgradle or legacy forgegradle
            if (loader.get() == ModLoaders.NONE_VANILLA) {
                target.requiredProp(Keys.neoformVersion).let { neoFormVersion.set(it) }
            } else {
                if (isModDevGradleRegular)
                    target.requiredProp(Keys.neoforgeVersion).let { neoForgeVersion.set(it) }
                if (isModDevGradleLegacy) {
                    target.requiredProp(Keys.forgeVersion).let { forgeVersion.set(it) }
                    target.requiredProp(Keys.mcpVersion).let { mcpVersion.set(it) }
                }
            }
        }

//region neoforge fixes
        moddevgradle {
            onEnable {
                tasks.named("createMinecraftArtifacts") {
                    dependsOn("stonecutterGenerate")
                }
                if (isModDevGradleRegular && loader.get() != ModLoaders.NONE_VANILLA) {
                    // On versions below 1.21.9, neoforge doesn't add non-minecraft dependencies to the classpath in development
                    // by default
                    // They are supposed to be added both as "implementation" and as "additionalRuntimeClasspath"
                    // Here we do this automatically
                    configurations.named("additionalRuntimeClasspath") {
                        extendsFrom(configurations.getByName(JvmConstants.IMPLEMENTATION_CONFIGURATION_NAME))
                    }
                }
            }
        }
        target.dependencies {
            loom {
                modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${target.requiredProp(Keys.fabricApiVersion)}")
            }
        }
    }
    target.tasks.withType<AppendModMetadataTask>().configureEach {
        if (loader.get() == ModLoaders.NONE_VANILLA) {
            enabled = false
        }
    }
    return SpecificPluginApplicationResult(modstitchModImplementation)
}


//todo setup runs for RFG also
fun ModstitchExtension.configureRuns(
    target: Project,
    runsData: NamedDomainObjectSet<RunConfigurationData>,
) {
    // Fabric
    loom {
        configureLoom {
            runs {
                runsData.all {
                    // maybeRegister here because fabric always registers the "client" and "server" runs, so if they
                    // already exist, we only configure them, and if the names are not "client" or "server" we register them
                    maybeRegister(this@runs, name) {
                        when (side.get()) {
                            io.github.archipelagominecraft.plugin.Side.Client -> {
                                client()
                            }

                            io.github.archipelagominecraft.plugin.Side.Server -> {
                                server()
                            }
                        }
                        runDir = workingDirectory.get().asFile.toRelativeString(target.layout.projectDirectory.asFile)
                        programArgs.clear()
                        programArgs.addAll(args.get())
                    }
                }
            }
        }
    }

    // NeoForge
    moddevgradle {
        configureNeoForge {
            runs {
                runsData.all {
                    register(name) {
                        when (side.get()) {
                            io.github.archipelagominecraft.plugin.Side.Client -> {
                                client()
                            }

                            io.github.archipelagominecraft.plugin.Side.Server -> {
                                server()
                            }
                        }
                        gameDirectory.set(workingDirectory)
                        programArguments.set(args)

                    }
                }
            }
        }
    }

}
