import io.archipelagominecraft.gradle.clientWorkingDirectory
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.modstitch
import io.archipelagominecraft.gradle.propMap
import io.archipelagominecraft.gradle.replacementProperties
import io.archipelagominecraft.gradle.serverWorkingDirectory

plugins {
    id("common-conventions")
    id("dev.isxander.modstitch.base")
}


modstitch.apply {
    minecraftVersion = modInfo.minecraftVersion

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaTarget = modInfo.javaVersion

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        replacementProperties.putAll(project.replacementProperties)
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure its up to date.
        fabricLoaderVersion = "0.16.10"

        // Configure loom like normal in this block.
        configureLoom {

        }
    }


    // NeoForge
    moddevgradle {
        enable {
            if (isModDevGradleRegular)
                propMap("deps.neoforge") { neoForgeVersion = it }
            if (isModDevGradleLegacy) {
                propMap("deps.forge") { forgeVersion = it }
                propMap("deps.mcp") { mcpVersion = it }
            }
            //if vanilla
//            propMap("deps.neoform") {neoFormVersion = it }
        }

        defaultRuns()
        configureNeoforge {
            runs.all {
                if(type.get() == "client"){
                    gameDirectory.set(project.clientWorkingDirectory)
                }
                else if(type.get() == "server"){
                    programArgument("nogui")
                    gameDirectory.set(project.serverWorkingDirectory)
                }
            }
        }
    }


    val mixinsFile = project.modInfo.mixins
    if (!mixinsFile.isNullOrBlank()) {
        mixin {
            // You do not need to specify mixins in any mods.json/toml file if this is set to
            // true, it will automatically be generated.
            addMixinsToModManifest = true

            configs.register(mixinsFile)

            // Most of the time you wont ever need loader specific mixins.
            // If you do, simply make the mixin file and add it like so for the respective loader:
            // if (isLoom) configs.register("examplemod-fabric")
            // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
            // if (isModDevGradleLegacy) configs.register("examplemod-forge")
        }
    }
}


// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        val modstitchModImplementation by configurations.getting
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${modInfo.requiredDep("fabricApi")}")
    }
}


modstitch.moddevgradle {
    tasks.named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }
}
