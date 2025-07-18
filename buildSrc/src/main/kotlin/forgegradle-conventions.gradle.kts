import io.archipelagominecraft.gradle.*

plugins {
    java
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin")
}

val forgeVersion = requiredProp("deps.forge")
val mixinVersionRange = requiredProp("deps.mixinRange")
val propMappingsChannel = requiredProp("deps.mappings.channel")
val propMappingsVersion = requiredProp("deps.mappings.version")

java.toolchain.languageVersion = JavaLanguageVersion.of(modInfo.javaVersion)
tasks.withType<JavaCompile>(){
    sourceCompatibility = modInfo.javaVersion.toString()
    targetCompatibility = modInfo.javaVersion.toString()
}





repositories {
    maven("https://maven.minecraftforge.net/")
    maven("https://repo.spongepowered.org/maven")
}

mixin {
    add(sourceSets.main.get(), "${modInfo.mixins}.refmap.json")
    config("${modInfo.mixins}.mixins.json")
}

dependencies {
    minecraft("net.minecraftforge:forge:${forgeVersion}") {
    }
    val mixinDep = "org.spongepowered:mixin:${mixinVersionRange}"
    annotationProcessor("${mixinDep}:processor") // Mixin processor for Forge
    minecraftEmbed(mixinDep) {
        exclude(group = "org.ow2.asm")
        exclude(module = "guava")
        exclude(module = "commons-io")
        exclude(module = "gson")
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
//            mods { //todo
//                "${rootProject.name}" {
//                    source(sourceSets.main.get())
//                }
//            }
        }

        runServer.apply {
            client(false)
            workingDirectory(project.serverWorkingDirectory.asFile)
            arg("nogui")
//            mods { //todo
//                "${rootProject.name}" {
//                    source(sourceSets.main.get())
//                }
//            }
        }
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
sourceSets.main {
    resources {
        srcDir(generateTemplates)
    }
}
tasks.jar {
    manifest.attributes(
        "ForceLoadAsMod" to "true",
        "FMLCorePluginContainsFMLMod" to "true",
        "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
        "TweakOrder" to "0"
    )
    finalizedBy("reobfJar")
}

////When Forge 1.12 loads mods from a directory that's been put on the classpath, it expects to find resources in the same directory.
////Default Gradle behavior puts resources in ./build/resources/main instead of ./build/classes/main/java. Let's change that.
sourceSets.all {
    this.output.setResourcesDir(this.output.classesDirs.getFiles().first())
}


