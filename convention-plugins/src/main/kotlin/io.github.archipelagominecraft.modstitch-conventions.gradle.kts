import dev.isxander.modstitch.base.AppendModMetadataTask
import io.archipelagominecraft.gradle.*
plugins {
    id("io.github.archipelagominecraft.common-conventions")
    id("dev.isxander.modstitch.base")
}


val modstitchModImplementation by configurations.existing

modstitchModImplementation {
    extendsFrom(configurations["multiModImplementation"])
}

modstitch.apply {
    minecraftVersion.set(modInfo.minecraftVersion)
    javaVersion.set(modInfo.javaVersion)

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        replacementProperties.putAll(project.replacementProperties)
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure its up to date.
        fabricLoaderVersion.set(requiredProp(Keys.fabricLoaderVersion))

        // Configure loom like normal in this block.
        configureLoom {

        }
    }


    // NeoForge
    moddevgradle {
            if (loader == LoaderConstants.VANILLA) {
                propMap(Keys.neoformVersion) { neoFormVersion.set(it) }
            } else {
                if (isModDevGradleRegular)
                    propMap(Keys.neoforgeVersion) { neoForgeVersion.set(it) }
                if (isModDevGradleLegacy) {
                    propMap(Keys.forgeVersion) { forgeVersion.set(it) }
                    propMap(Keys.mcpVersion) { mcpVersion.set(it) }
                }
            }
            //if vanilla
//            propMap("deps.neoform") {neoFormVersion = it }

        if(loader != LoaderConstants.VANILLA) {
            defaultRuns()
        }
        configureNeoForge {
            runs.all {
                if(type.get() == "client"){
                    gameDirectory.set(project.clientWorkingDirectory)
                }
                else if(type.get() == "server"){
                    programArgument("nogui")
                    gameDirectory.set(project.serverWorkingDirectory)
                }
                disableIdeRun()
                additionalRuntimeClasspathConfiguration.extendsFrom(configurations.runtimeClasspath.get())
            }
        }
    }


    val mixinsFile = project.modInfo.mixinsFileName
    if (!mixinsFile.isNullOrBlank()) {
        mixin {
            // You do not need to specify mixins in any mods.json/toml file if this is set to
            // true, it will automatically be generated.
            addMixinsToModManifest.set(true)

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

if (loader == LoaderConstants.VANILLA) {
    tasks.withType<AppendModMetadataTask> {
        enabled = false
    }
    dependencies {
        val compileOnly by configurations.existing
        compileOnly("org.spongepowered:mixin:${requiredProp(Keys.vanillaMixinsVersion)}")
    }
}


dependencies {
    modstitch.loom {
        val modstitchModImplementation by configurations.getting
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${requiredProp(Keys.fabricApiVersion)}")
    }
}


// fix stonecutterGenerate task dependencies
tasks.named<ProcessResources>("generateModMetadata") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn("stonecutterGenerate")
}
modstitch.moddevgradle {
    modstitch.onEnable {
        tasks.named("createMinecraftArtifacts") {
            dependsOn("stonecutterGenerate")
        }
    }
}
