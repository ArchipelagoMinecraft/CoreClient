import io.archipelagominecraft.gradle.clientWorkingDirectory
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.replacementProperties
import io.archipelagominecraft.gradle.requiredProp
import io.archipelagominecraft.gradle.serverWorkingDirectory

plugins {
    java
    id("net.minecraftforge.gradle")
}

val forgeVersion = requiredProp("deps.forge")
val propMappingsChannel = requiredProp("deps.mappings.channel")
val propMappingsVersion = requiredProp("deps.mappings.version")

java.toolchain.languageVersion = JavaLanguageVersion.of(modInfo.javaVersion)
tasks.withType<JavaCompile>(){
    sourceCompatibility = modInfo.javaVersion.toString()
    targetCompatibility = modInfo.javaVersion.toString()
}




repositories {
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    minecraft("net.minecraftforge:forge:${forgeVersion}")
}

val runClient = minecraft.runs.create("client")
val runServer = minecraft.runs.create("server")

minecraft {
    mappings(propMappingsChannel, propMappingsVersion)

    runs{
        runClient.apply {
            client(true)
            workingDirectory(project.clientWorkingDirectory.asFile)
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


////When Forge 1.12 loads mods from a directory that's been put on the classpath, it expects to find resources in the same directory.
////Default Gradle behavior puts resources in ./build/resources/main instead of ./build/classes/main/java. Let's change that.
sourceSets.all {
    this.output.setResourcesDir(this.output.classesDirs.getFiles().first())
}


