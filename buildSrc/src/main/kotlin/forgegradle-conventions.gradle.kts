import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.archipelagominecraft.gradle.*

plugins {
    java
    id("common-conventions")
    id("com.gtnewhorizons.retrofuturagradle")
}

val forgeVersion = requiredProp("deps.forge")
val dfuVersion = requiredProp("deps.datafixerupper")
val propMappingsChannel = requiredProp("deps.mappings.channel")
val propMappingsVersion = requiredProp("deps.mappings.version")

java.toolchain.languageVersion = JavaLanguageVersion.of(modInfo.javaVersion)
tasks.withType<JavaCompile>(){
    sourceCompatibility = modInfo.javaVersion.toString()
    targetCompatibility = modInfo.javaVersion.toString()
}




repositories {
    maven("https://repo.spongepowered.org/maven")
    exclusiveContent {
        forRepository {
            maven("https://maven.cleanroommc.com")
        }
        filter {
            includeModule("zone.rong", "mixinbooter")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://libraries.minecraft.net")
        }
        filter {
            includeModule("com.mojang", "datafixerupper")
        }
    }
}


dependencies {
    val shade by configurations.existing
    shade("com.mojang:datafixerupper:${dfuVersion}"){
        isTransitive = false
    }

    //mixins
    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
    annotationProcessor("com.google.guava:guava:32.1.2-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.9")

    val mixins: String = modUtils.enableMixins("zone.rong:mixinbooter:10.6", "${modInfo.mixins}.refmap.json") as String
    api(mixins) {
        isTransitive = false
    }
    annotationProcessor(mixins) {
        isTransitive = false
    }
}


tasks.runClient {
    setWorkingDir(project.clientWorkingDirectory)
}

tasks.runServer{
    setWorkingDir(project.serverWorkingDirectory)
}

minecraft {
    mcVersion.set("1.12.2")
    mcpMappingChannel.set(propMappingsChannel)
    mcpMappingVersion.set(propMappingsVersion)
}

val mainSourceSet = project.extensions.getByType<SourceSetContainer>()["main"]
val templates = project.objects.sourceDirectorySet("templates", "Mod metadata resource templates")
mainSourceSet.extensions.add("templates", templates)
templates.srcDir("src/main/templates")
val generateTemplates by tasks.registering(ProcessResources::class) {
    inputs.property("allProperties", project.replacementProperties)

    from(templates)
    into(project.layout.buildDirectory.dir("generated/metadata"))
    include("mcmod.info","pack.mcmeta")
    doFirst {
        expand(project.replacementProperties)
    }
}

val relocated = modInfo.packageName + ".relocated"

sourceSets.main {
    resources {
        srcDir(generateTemplates)
    }
}
tasks.jar {
    manifest.attributes(
        "ForceLoadAsMod" to "true",
        "FMLCorePlugin" to "${modInfo.packageName}.loaders.legacy.LegacyForgeCorePlugin",
        "FMLCorePluginContainsFMLMod" to "true",
    )
}

tasks.shadowJar {
    relocate("com.mojang", "$relocated.com.mojang")
}
tasks.reobfJar {
    inputJar.set(tasks.shadowJar.flatMap { it.archiveFile })
    dependsOn(tasks.shadowJar)
}

