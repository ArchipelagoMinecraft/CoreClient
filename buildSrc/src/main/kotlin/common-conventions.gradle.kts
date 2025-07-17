import dev.isxander.controlify.branchProj
import dev.isxander.controlify.isFabric
import dev.isxander.controlify.isForge
import dev.isxander.controlify.isForgeLike
import dev.isxander.controlify.isLegacy
import dev.isxander.controlify.isModern
import dev.isxander.controlify.isNeoForge
import dev.isxander.controlify.loader
import dev.isxander.controlify.stonecutter
import java.util.Properties

plugins{
    base
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}
//todo add plugin to generate class with constants, like version, mod id, etc from the properties

// from https://github.com/meza/Stonecraft/blob/ea2eb86e3c4a479dd2e2dfecd42f41450ddc968d/src/main/kotlin/gg/meza/stonecraft/configurations/Dependencies.kt#L75
public fun loadSpecificDependencyVersions(project: Project, minecraftVersion: String) {
    val customPropsFile = project.rootProject.file("versions/dependencies/$minecraftVersion.properties")

    if (customPropsFile.exists()) {
        val customProps = Properties().apply {
            customPropsFile.inputStream().use { load(it) }
        }
        customProps.forEach { key, value ->
            project.extra[key.toString()] = value
        }
    }
}

loadSpecificDependencyVersions(project,stonecutter.current.version)



// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants

stonecutter.apply {
    consts( //todo fix deprecated
        "fabric" to isFabric,
        "neoforge" to isNeoForge,
        "forge" to isForge,
        "forgeLike" to isForgeLike,
        "legacy" to isLegacy,
        "modern" to isModern
    )
}

val modstitchPlatform = when(loader){
    "neoforge" -> "moddevgradle"
    "forge" -> "moddevgradle-legacy"
    "fabric" -> "loom"
    "legacy" -> "" // to not crash
    else -> throw IllegalArgumentException("Unknown loader: $loader")
}
project.extra["modstitch.platform"] = modstitchPlatform
