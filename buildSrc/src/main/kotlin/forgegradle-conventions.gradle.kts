import io.archipelagominecraft.gradle.*

plugins {
    java
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
    id("com.gradleup.shadow")
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

val shade by configurations.creating
configurations.implementation {
    extendsFrom(shade)
}


repositories {
    maven("https://maven.minecraftforge.net/")
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

mixin {
    add(sourceSets.main.get(), "${modInfo.mixins}.refmap.json")
    config("${modInfo.mixins}.mixins.json")
}

dependencies {
    minecraft("net.minecraftforge:forge:${forgeVersion}")
    shade("com.mojang:datafixerupper:${dfuVersion}")

    //mixins
    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
    annotationProcessor("com.google.guava:guava:32.1.2-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.9")

    implementation("zone.rong:mixinbooter:10.6") {
        isTransitive = false
    }
    annotationProcessor("zone.rong:mixinbooter:10.6") {
        isTransitive = false
    }
}

val runClient = minecraft.runs.create("client")
val runServer = minecraft.runs.create("server")

minecraft {
    mappings(propMappingsChannel, propMappingsVersion)

    runs{
        runClient.apply {
            client(true)
            workingDirectory(project.clientWorkingDirectory.asFile)
            property("forge.logging.console.level", "debug")
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            arg("-torg.spongepowered.asm.launch.MixinTweaker")
        }

        runServer.apply {
            client(false)
            workingDirectory(project.serverWorkingDirectory.asFile)
            arg("nogui")
            arg("-torg.spongepowered.asm.launch.MixinTweaker")
        }
    }

}

tasks.configureEach {
    if (name == runClient.taskName || name == runServer.taskName) {
        dependsOn(tasks.jar) // Or mixin config file is not found
    }
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
    archiveClassifier.set("slim")

    manifest.attributes(
        "ForceLoadAsMod" to "true",
        "FMLCorePlugin" to "${modInfo.packageName}.loaders.legacy.LegacyForgeCorePlugin",
        "FMLCorePluginContainsFMLMod" to "true",
    )
    finalizedBy("reobfJar")
}


val reobfShadowJar = reobf.create("shadowJar")
tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(shade)
    relocate("com.mojang", "$relocated.com.mojang")
    finalizedBy(reobfShadowJar)
}
tasks.assemble {
    dependsOn(tasks.shadowJar)
}


////When Forge 1.12 loads mods from a directory that's been put on the classpath, it expects to find resources in the same directory.
////Default Gradle behavior puts resources in ./build/resources/main instead of ./build/classes/main/java. Let's change that.
sourceSets.all {
    this.output.setResourcesDir(this.output.classesDirs.getFiles().first())
}


