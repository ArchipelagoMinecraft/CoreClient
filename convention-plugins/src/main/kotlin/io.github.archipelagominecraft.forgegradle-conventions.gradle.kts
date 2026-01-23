import io.archipelagominecraft.gradle.*

plugins {
    java
    id("io.github.archipelagominecraft.common-conventions")
    id("com.gtnewhorizons.retrofuturagradle")
}

val dfuVersion = requiredProp(Keys.dfuVersion)
val propMappingsChannel = requiredProp(Keys.mappingsChannel)
val propMappingsVersion = requiredProp(Keys.mappingsVersion)

//java.toolchain.languageVersion = JavaLanguageVersion.of(modInfo.javaVersion)
//tasks.withType<JavaCompile>(){
//    sourceCompatibility = modInfo.javaVersion.toString()
//    targetCompatibility = modInfo.javaVersion.toString()
//}

//val multiModImplementation = configurations.getByName("multiModImplementation")

//afterEvaluate {
//    dependencies {
//
//        multiModImplementation.dependencies.forEach { dep ->
//            // Convert the dependency notation via rfg.deobf
//            val transformed = when (dep) {
//                is ModuleDependency -> rfg.deobf("${dep.group}:${dep.name}:${dep.version}")
//                else -> throw IllegalArgumentException("Unsupported dependency type: $dep")
//            }
//            implementation(transformed)
//        }
//    }
//}


//
//repositories {
//    maven("https://repo.spongepowered.org/maven")
//    exclusiveContent {
//        forRepository {
//            maven("https://maven.cleanroommc.com")
//        }
//        filter {
//            includeModule("zone.rong", "mixinbooter")
//        }
//    }
//    exclusiveContent {
//        forRepository {
//            maven("https://libraries.minecraft.net")
//        }
//        filter {
//            includeModule("com.mojang", "datafixerupper")
//        }
//    }
//}
//
//
//dependencies {
//    val shade by configurations.existing
//    shade("com.mojang:datafixerupper:${dfuVersion}"){
//        isTransitive = false
//    }
//
//    //mixins
//    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
//    annotationProcessor("com.google.guava:guava:32.1.2-jre")
//    annotationProcessor("com.google.code.gson:gson:2.8.9")
//
//    val mixins: String = modUtils.enableMixins("zone.rong:mixinbooter:10.6", "${modInfo.mixinsFileName}.refmap.json") as String
//    api(mixins) {
//        isTransitive = false
//    }
//    annotationProcessor(mixins) {
//        isTransitive = false
//    }
//}
//
//
//tasks.runClient {
//    setWorkingDir(project.clientWorkingDirectory)
//}
//
//tasks.runServer{
//    setWorkingDir(project.serverWorkingDirectory)
//}
//
//minecraft {
//    mcVersion.set(modInfo.minecraftVersion)
//    mcpMappingChannel.set(propMappingsChannel)
//    mcpMappingVersion.set(propMappingsVersion)
//    if (loader == LoaderConstants.FORGE){
//        usesFml.set(true)
//        usesForge.set(true)
//    } else if (loader == LoaderConstants.VANILLA){
//        usesFml.set(true)
//        usesForge.set(false)
//        useDependencyAccessTransformers.set(false)
//
//    } else {
//        error("Unsupported loader type for retrofuturagradle: $loader")
//    }
//}
//
//if(loader == LoaderConstants.VANILLA){
//    afterEvaluate {
//        tasks.named("runServer"){
//            enabled = false
//        }
//        tasks.named("runClient"){
//            enabled = false
//        }
//    }
//}

//val mainSourceSet = project.extensions.getByType<SourceSetContainer>()["main"]
//val templates = project.objects.sourceDirectorySet("templates", "Mod metadata resource templates")
//mainSourceSet.extensions.add("templates", templates)
//templates.srcDir("src/main/templates")
//if(loader != LoaderConstants.VANILLA) {
//    val generateTemplates by tasks.registering(ProcessResources::class) {
//        inputs.property("allProperties", project.replacementProperties)
//
//        from(templates)
//        into(project.layout.buildDirectory.dir("generated/metadata"))
//        include("mcmod.info", "pack.mcmeta")
//        doFirst {
//            expand(project.replacementProperties)
//        }
//    }
//    sourceSets.main {
//        resources {
//            srcDir(generateTemplates)
//        }
//    }
//}
//
//val relocated = modInfo.packageName + ".relocated"
//
//if(loader != LoaderConstants.VANILLA) {
//
//    tasks.jar {
//        manifest.attributes(
//            "ForceLoadAsMod" to "true",
//            "FMLCorePlugin" to requiredProp(Keys.fmlCorePluginClass),
//            "FMLCorePluginContainsFMLMod" to "true",
//        )
//    }
//}

//tasks.shadowJar {
//    relocate("com.mojang", "$relocated.com.mojang")
//}
//tasks.reobfJar {
//    inputJar.set(tasks.shadowJar.flatMap { it.archiveFile })
//    dependsOn(tasks.shadowJar)
//}


//tasks.getByName("createMcLauncherKotlin"){
//    dependsOn("createMcLauncherFiles")
//}

//tasks.getByName("kspMcLauncherKotlin"){
//    dependsOn("createMcLauncherFiles")
//}
//

tasks.getByName("processPatchedMcResources") {
    beforeEvaluate {
        mkdir(layout.buildDirectory.dir("generated/ksp"))
    }
}



tasks.configureEach {
    if (name == "kspMcLauncherKotlin") {
        dependsOn("createMcLauncherFiles")
        dependsOn("processPatchedMcResources")
    }
    if(name == "kspPatchedMcKotlin"){
        dependsOn("decompressDecompiledSources")
    }
    if(name == "kspInjectedTagsKotlin"){
        dependsOn("processPatchedMcResources")
    }
    if(name == "stonecutterMergeInjectedInterfaces"){
        dependsOn("processInjectedInterfacesResources")
    }
    if(name == "compileInjectedInterfacesKotlin"){
        dependsOn("stonecutterMergeInjectedInterfaces")
    }
    if(name == "stonecutterGenerateInjectedInterfaces"){
        dependsOn("stonecutterMergeInjectedInterfaces")
    }
    if(name == "stonecutterMergeMcLauncher"){
        dependsOn("processMcLauncherResources")
    }
    if(name == "stonecutterMergeMcLauncher"){
        dependsOn("compileMcLauncherKotlin")
    }
}

