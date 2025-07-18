import gradle.kotlin.dsl.accessors._26a65878ab300c189b82af1c4da50d69.j52j
import io.archipelagominecraft.gradle.branchProj
import io.archipelagominecraft.gradle.isFabric
import io.archipelagominecraft.gradle.isForge
import io.archipelagominecraft.gradle.isForgeLike
import io.archipelagominecraft.gradle.isLegacy
import io.archipelagominecraft.gradle.isModern
import io.archipelagominecraft.gradle.isNeoForge
import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.requiredProp
import io.archipelagominecraft.gradle.serverWorkingDirectory
import io.archipelagominecraft.gradle.stonecutter
import org.cthing.gradle.plugins.buildconstants.SourceAccess
import java.util.Properties

plugins{
    base
    id("me.modmuss50.mod-publish-plugin")
    id("org.cthing.build-constants")
    id("dev.kikugie.j52j")
    `maven-publish`
}

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

tasks.generateBuildConstants{
    classname = modInfo.packageName + "." + modInfo.name + "Constants"
    source(files("buildSrc/**"),files("versions/dependencies/**"))
    additionalConstants.put("MOD_ID", modInfo.id)
    additionalConstants.put("MOD_NAME", modInfo.name)
    additionalConstants.put("MOD_VERSION", modInfo.version)
}



val acceptEula: TaskProvider<Task> by tasks.registering {
    doLast {
        val eulaFile = serverWorkingDirectory.file("eula.txt").asFile
        eulaFile.parentFile.mkdirs()
        eulaFile.createNewFile()
        eulaFile.writeText("eula=true")
    }
}

tasks.configureEach {
    if (this.name == "runServer") {
        dependsOn(acceptEula)
    }
}
//
j52j{
    params {
        prettyPrinting = true
    }
}

stonecutter.apply {

    filters{
        include("resources/**.json")
    }

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
    "legacy" -> null
    else -> throw IllegalArgumentException("Unknown loader: $loader")
}


if(modstitchPlatform != null) {
    project.extra["modstitch.platform"] = modstitchPlatform
}



