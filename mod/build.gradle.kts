import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo

plugins {
    kotlin("jvm")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
    id("com.gradleup.shadow")
    `maven-publish`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xnested-type-aliases")
    }

}

buildMultiversion {
//    mixinsPackage = "io.github.archipelagominecraft.core.mixin"
}


val shade by configurations.registering
configurations.implementation {
    extendsFrom(shade.get())
}

dependencies {

    // we shade the kotlin stdlib in our jar (to not depend on things like KotlinForForge)
    // This means extra bloat if another mod provides kotlin, yes, but this also means that our mod is
    // completely standalone, and this is especially helpful on older minecraft versions
    shade(kotlin("stdlib-jdk8"))

    shade(project(":relocated-deps", configuration = "shadow"))
    val dependencyNotation = project(":api:${modInfo.minecraftVersion}-vanilla")
//    val p = when (pluginType) {
//        PluginTypes.MODSTITCH -> {
//            dependencyNotation
//        }
//        PluginTypes.RFG -> {
//            rfg.deobf(dependencyNotation)
//        }
//    }
    shade(dependencyNotation) {
        isTransitive = false
    }
}



val kotlinRelocateBase = "io.github.archipelagominecraft.core.shadow.kotlin"


// Rename output jars from
// XXX.jar -> non-shadowed
// XXX-all.jar -> shadowed
// to
// XXX-slim.jar -> non-shadowed
// XXX.jar -> shadowed
project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
    archiveClassifier.set("slim")
}
project.tasks.named<ShadowJar>(ShadowJar.SHADOW_JAR_TASK_NAME) {
    archiveClassifier.set("")
    configurations.set(shade.map { listOf(it) })
//    todo test conflict with other mods like kotlinForForge to see if relocation is really needed
    relocate("kotlin", "$kotlinRelocateBase.kotlin")
    relocate("org.jetbrains", "$kotlinRelocateBase.jetbrains")
    relocate("intellij", "$kotlinRelocateBase.intellij")
    relocate("org.intellij", "$kotlinRelocateBase.org.intellij")
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/ArchipelagoMinecraft/CoreClient")
            credentials {
                username = ""
                password = providers.gradleProperty("gpr.token").orElse(provider {
                    throw IllegalStateException(
                        "To get packages from github packages," +
                                " a Personal Access Token (PAT) is required, even for public packages, please create one" +
                                "and put it in \$GRADLE_USER_HOME/gradle.properties (NOT in the project's gradle.properties)"
                    )
                }).get()
            }
        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("mod") {
            from(components["kotlin"])
            groupId = "io.github.archipelagominecraft"
            artifactId = "client-core-${modInfo.minecraftVersion}-${loader}"
            version = modInfo.version
        }
    }
}

