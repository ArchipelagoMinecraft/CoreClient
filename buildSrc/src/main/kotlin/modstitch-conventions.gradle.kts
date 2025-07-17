import dev.isxander.controlify.branchProj
import dev.isxander.controlify.mcVersion
import dev.isxander.controlify.modstitch
import dev.isxander.controlify.prop
import dev.isxander.controlify.propMap
import dev.isxander.controlify.replacementProperties
import dev.isxander.controlify.requiredProp
import dev.isxander.controlify.stonecutter
import org.gradle.kotlin.dsl.register
import java.util.Properties

plugins {
    id("common-conventions")
    id("dev.isxander.modstitch.base")
}



modstitch.apply {
    minecraftVersion = mcVersion

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaTarget = when (mcVersion) { //todo properties
        "1.20.1" -> 17
        "1.21.4" -> 21
        else -> throw IllegalArgumentException("Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!")
    }


    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            putAll(project.replacementProperties)
        }
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


    moddevgradle {
        enable {
            propMap("deps.neoforge") { neoForgeVersion = it }
            propMap("deps.forge") { forgeVersion = it }
        }

        defaultRuns()
        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }


    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true

        configs.register("archipelago_minecraft_core") //todo from property

        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }
}


// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        val modstitchModImplementation by configurations.getting
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.112.0+1.21.4")
    }
}


modstitch.moddevgradle {
    tasks.named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }
}
