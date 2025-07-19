import io.archipelagominecraft.gradle.requiredProp
import io.archipelagominecraft.gradle.*
import org.cthing.gradle.plugins.buildconstants.SourceAccess
import java.util.Properties

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

val modstitchPlatform = when(loader){
    "neoforge" -> "moddevgradle"
    "forge" -> "moddevgradle-legacy"
    "fabric" -> "loom"
    "vanilla" -> "moddevgradle"
    "legacy" -> null
    else -> throw IllegalArgumentException("Unknown loader: $loader")
}


if(modstitchPlatform != null) {
    project.extra["modstitch.platform"] = modstitchPlatform
}



