package io.github.archipelagominecraft.buildplugin.configs

import dev.isxander.modstitch.base.AppendModMetadataTask
import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.isxander.modstitch.util.Side
import io.github.archipelagominecraft.buildplugin.Keys
import io.github.archipelagominecraft.buildplugin.modInfo
import io.github.archipelagominecraft.buildplugin.replacementProperties
import io.github.archipelagominecraft.buildplugin.requiredProp
import org.gradle.api.Project
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

//modstitch config
fun modstitchConfiguration(
    target: Project,
    javaVersion: Provider<Int>,
    isVanilla: Boolean,
): SpecificPluginApplicationResult {

    val modstitchModImplementation = target.configurations.named("modstitchModImplementation")
    target.extensions.configure<ModstitchExtension>() {

        val modInfo = target.modInfo
        minecraftVersion.set(modInfo.minecraftVersion)
        this.javaVersion.set(javaVersion)

        metadata {
            modId.set(modInfo.id)
            replacementProperties.putAll(target.replacementProperties)
        }

        parchment {
            mappingsVersion.set(target.requiredProp(Keys.parchmentMappingsVersion))
        }


        // Fabric Loom (Fabric)
        loom {
            fabricLoaderVersion.set(target.requiredProp(Keys.fabricLoaderVersion))
        }

        // NeoForge
        moddevgradle {
            // If vanilla, we use neoform, else we use moddevgradle or legacy forgegradle
            if (isVanilla) {
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
        retrofuturagradle {
            forgeVersion.set(target.requiredProp(Keys.forgeVersion))
            mcpVersion.set(target.requiredProp(Keys.mcpVersion))
            mcpChannel.set(target.requiredProp(Keys.mcpChannel))
        }

        moddevgradle {
            onEnable {
                tasks.named("createMinecraftArtifacts") {
                    dependsOn("stonecutterGenerate")
                }
                if (isModDevGradleRegular && !isVanilla) {
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
                val requiredProp = target.requiredProp(Keys.fabricApiVersion).get()
                modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:$requiredProp")
            }
        }

        runs {
            register("client") {
                side.set(Side.Client)
                gameDirectory.set(target.layout.projectDirectory.dir("runClient"))
            }
            register("server") {
                side.set(Side.Server)
                gameDirectory.set(target.layout.projectDirectory.dir("runServer"))
                programArgs.add("nogui")
            }
        }
    }
    if (isVanilla) {
        target.tasks.withType<AppendModMetadataTask>().configureEach {
            enabled = false
        }
    }
    return SpecificPluginApplicationResult(modstitchModImplementation)
}

