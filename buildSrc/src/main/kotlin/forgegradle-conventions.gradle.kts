import dev.isxander.controlify.requiredProp

plugins {
    java
    id("net.minecraftforge.gradle")
}

val forgeVersion = requiredProp("deps.forge")
val mappingsVersion = requiredProp("deps.mappings")

java.toolchain.languageVersion = JavaLanguageVersion.of(8)
tasks.withType<JavaCompile>(){
    sourceCompatibility = "8"
    targetCompatibility = "8"
}

repositories {
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    minecraft("net.minecraftforge:forge:")
}

minecraft {
    mappings("stable", "39-1.12")

    runs{
        create("client"){
            client(true)
            workingDirectory(project.file("run"))
//            mods { //todo
//                "${rootProject.name}" {
//                    source(sourceSets.main.get())
//                }
//            }
        }

        create("server"){
            client(true)
            workingDirectory(project.file("run"))
//            mods { //todo
//                "${rootProject.name}" {
//                    source(sourceSets.main.get())
//                }
//            }
        }
    }

}

//
tasks.processResources {
    //todo grab mcmod.info from template folder and apply common replacements
//    inputs.property "version", project.version
//
//    filesMatching("mcmod.info") {
//        expand "version": project.version
//    }
}
//
////When Forge 1.12 loads mods from a directory that's been put on the classpath, it expects to find resources in the same directory.
////Default Gradle behavior puts resources in ./build/resources/main instead of ./build/classes/main/java. Let's change that.
sourceSets.all {
    this.output.setResourcesDir(this.output.classesDirs.getFiles().first())
}
