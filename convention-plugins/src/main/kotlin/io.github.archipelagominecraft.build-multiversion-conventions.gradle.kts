//import dev.kikugie.fletching_table.extension.FletchingTableExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.archipelagominecraft.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.github.archipelagominecraft.conditional-modstitch")
    id("org.cthing.build-constants")
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}


//tasks.named("kspKotlin"){
//    dependsOn("generateBuildConstants")
//}


val stonecutter = extensions.getByType<StonecutterBuildExtension>()
//val fletchingTable = extensions.getByType<FletchingTableExtension>()

stonecutter.apply {
    filters {
        include("resources/**.json")
        include("templates/**.mcmeta")
        include("templates/**.json")
        include("templates/**.info")
    }
    constants["fabric"] = isFabric
    constants["neoforge"] = isNeoForge
    constants["forge"] = isForge
    constants["forgeLike"] = isForgeLike
}
//fletchingTable.apply {
//    j52j.register("main") {
////        this@register.prettyPrint.set(true)
//        extension("json", "resource/**/*.json5")
//    }
//}

tasks.configureEach {
//    if(name.startsWith("stonecutterGenerate")) {
//        dependsOn(name.replace("Generate","Merge"))
//    }
//    if(name.startsWith("stonecutterPrepareTest")) {
//        dependsOn(name.replace("Prepare","Merge"))
//    }

    if(name == "compileInjectedTagsKotlin"){
        dependsOn("injectTags")
    }
    if(name == "kspInjectedTagsKotlin"){
        dependsOn("injectTags")
    }
}

if (loader != LoaderConstants.VANILLA) {

    tasks.withType<KotlinCompile> {
        dependsOn(tasks.generateBuildConstants)
    }
    tasks.configureEach {
        if (name == "kspKotlin") {
            dependsOn(tasks.generateBuildConstants)
        }
    }
    tasks.generateBuildConstants {
        classname.set(modInfo.packageName + "." + modInfo.name + "Constants")
        source(files("buildSrc/**"), files("versions/dependencies/**"))
        additionalConstants.put("MOD_ID", modInfo.id)
        additionalConstants.put("MOD_NAME", modInfo.name)
        additionalConstants.put("MOD_VERSION", modInfo.version)
        additionalConstants.put("MOD_MIXINS_FILE", modInfo.mixinsFileName ?: "")
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
}
