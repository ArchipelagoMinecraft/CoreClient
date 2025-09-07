import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.archipelagominecraft.gradle.*
import java.util.Properties

plugins {
    base
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.fletching-table")
    id("com.gradleup.shadow")
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

val modstitchPlatform = when(loader){
    LoaderConstants.NEOFORGE -> "moddevgradle"
    LoaderConstants.FORGE -> "moddevgradle-legacy"
    LoaderConstants.FABRIC -> "loom"
    LoaderConstants.VANILLA -> "moddevgradle"
    "legacy" -> null
    else -> throw IllegalArgumentException("Unknown loader for modstitch: $loader")
}

if(modstitchPlatform != null) {
    project.extra["modstitch.platform"] = modstitchPlatform
}


val implementation by configurations.existing

configurations.create("multiModImplementation")

val shade by configurations.creating
implementation {
    extendsFrom(shade)
}
tasks.existing(Jar::class){
    archiveClassifier.set("slim")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    configurations = listOf(shade)
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

val runtimeClassPath by configurations.registering

kotlin {
    @Suppress("UnstableApiUsage")
    jvmToolchain(modInfo.javaVersion)
    compilerOptions{
        freeCompilerArgs.add("-Xnested-type-aliases")
    }
}
dependencies{
    val kotlinStdlib = kotlin("stdlib-jdk8")
    shade(kotlinStdlib)
    runtimeClassPath(kotlinStdlib)
}
tasks.shadowJar {
    relocate("kotlin", "${modInfo.packageName}.relocated.kotlin")
    relocate("org.jetbrains", "${modInfo.packageName}.relocated.jetbrains")
    relocate("intellij", "${modInfo.packageName}.relocated.intellij")
}


