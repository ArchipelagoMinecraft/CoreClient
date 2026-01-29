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
import org.gradle.kotlin.dsl.accessors.runtime.maybeRegister
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

fun modstitchConfiguration(
    target: Project,
    javaVersion: Provider<Int>,
    loader: Provider<ModLoaders>,
    runs: NamedDomainObjectSet<RunConfigurationData>,
): SpecificPluginApplicationResult {
    // We need to convert the loader into the correct modstitch platform property, **before** applying the
    // modstitch plugin
    val modstitchModImplementation = target.configurations.named("modstitchModImplementation")
//    target.extensions.configure<KotlinJvmExtension>() {
//        @Suppress("UnstableApiUsage")
//        jvmToolchain {
//            languageVersion.set(javaVersion.map { JavaLanguageVersion.of(it) })
//        }
//    }
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
                neoFormVersion.set(target.requiredProp(Keys.neoformVersion))
            } else {
                if (isModDevGradleRegular)
                    neoForgeVersion.set(target.requiredProp(Keys.neoforgeVersion))
                if (isModDevGradleLegacy) {
                    forgeVersion.set(target.requiredProp(Keys.forgeVersion))
                    mcpVersion.set(target.requiredProp(Keys.mcpVersion))
                }
            }
        }
        target.retroFuturaGradle {
            forgeVersion.set(target.requiredProp(Keys.forgeVersion))
            mcpVersion.set(target.requiredProp(Keys.mcpVersion))
            mcpChannel.set(target.requiredProp(Keys.mcpChannel))
            if (loader.get() != ModLoaders.NONE_VANILLA) {
                usesForge.set(true)
                usesFML.set(true)
            } else {
                usesForge.set(true)
                //required for launcher
                usesFML.set(true)
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
        if (loader.get() == ModLoaders.NEOFORGE) {
            target.dependencies {
                components {
                    withModule("net.neoforged:minecraft-dependencies") {
                        allVariants {
                            withDependencyConstraints {
                                removeIf { it.group == "org.ow2.asm" }
                            }
                        }
                    }
                }
            }
        }
        target.dependencies {
            loom {
                val requiredProp = target.requiredProp(Keys.fabricApiVersion).get()
                modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:$requiredProp")
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
