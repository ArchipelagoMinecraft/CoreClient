import org.gradle.kotlin.dsl.kotlin

plugins{
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "2.2.0"
}

group = "io.github.archipelagominecraft"
version = "1.0.1-SNAPSHOT"

repositories{
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
    maven("https://maven.neoforged.net/releases")
//    maven("https://maven.isxander.dev/releases/")
    maven("https://maven.kikugie.dev/releases")
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://jitpack.io")
    maven{
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
        mavenContent {
            includeGroupByRegex("com\\.gtnewhorizons\\..+")
            includeGroup("com.gtnewhorizons")
        }
    }
}

gradlePlugin {
    plugins.create("io.github.archipelagominecraft.settings-conventions") {
        id = "io.github.archipelagominecraft.settings-conventions"
        implementationClass = "io.github.archipelagominecraft.buildplugin.SettingsPlugin"
    }
    plugins.create("io.github.archipelagominecraft.build-multiversion-conventions") {
        id = "io.github.archipelagominecraft.build-multiversion-conventions"
        implementationClass = "io.github.archipelagominecraft.buildplugin.BuildMultiversionPlugin"
    }
}


publishing {
    repositories{
        maven{
            url = uri("https://maven.pkg.github.com/ArchipelagoMinecraft/CoreClient")
            credentials {
                username = ""
                password = providers.gradleProperty("gpr.token").orNull
            }
        }
    }
}


dependencies{
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"
//    implementation(plugin("dev.isxander.modstitch.base", "0.8.4"))
    // fork with RFG support
    implementation("com.github.LelouBil.Modstitch:dev.isxander.modstitch.base.gradle.plugin:a4141ebb")

    implementation(plugin("com.google.devtools.ksp", "2.2.0-2.0.2"))
    implementation(plugin("dev.kikugie.fletching-table","0.1.0-alpha.22"))

    implementation(plugin("dev.kikugie.stonecutter", "0.8.3"))
    implementation(plugin("org.cthing.build-constants", "2.0.0"))
    implementation(plugin("org.jetbrains.kotlin.jvm", "2.2.0"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0") // for stonecutter
    implementation("net.neoforged:srgutils:1.0.11")

    //settings plugins
    implementation(plugin("org.danilopianini.gradle-pre-commit-git-hooks", "2.0.28"))
    implementation(plugin("org.jetbrains.gradle.plugin.idea-ext", "1.2"))
    implementation(plugin("org.gradle.toolchains.foojay-resolver-convention", "1.0.0"))
}
