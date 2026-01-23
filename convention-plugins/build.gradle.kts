import org.gradle.internal.impldep.org.apache.maven.model.PluginConfiguration
import org.gradle.kotlin.dsl.kotlin

plugins{
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "2.2.0"
}

group = "io.github.archipelagominecraft"
version = "1.0.1-SNAPSHOT"

@Suppress("UnstableApiUsage")
repositories{
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.isxander.dev/releases/")
    exclusiveContent {
        forRepositories(
            maven("https://repo.spongepowered.org/maven")
        )
        filter{
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    maven("https://maven.kikugie.dev/releases")
    maven("https://maven.kikugie.dev/snapshots")
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
        implementationClass = "SettingsPlugin"
    }
}


publishing {
    repositories{
        maven{
            url = uri("https://maven.pkg.github.com/ArchipelagoMinecraft/CoreClient")
            credentials {
                username = ""
                password = providers.gradleProperty("gpr.token").get()
            }
        }
        mavenLocal()
    }
}


dependencies{
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"
    implementation(plugin("com.google.devtools.ksp", "2.2.0-2.0.2"))
    implementation(plugin("dev.kikugie.fletching-table","0.1.0-alpha.15"))
    implementation(plugin("dev.kikugie.stonecutter", "0.7.11"))
    implementation(plugin("org.cthing.build-constants", "2.0.0"))
    implementation(plugin("me.modmuss50.mod-publish-plugin", "0.8.4"))
    implementation(plugin("org.jetbrains.kotlin.jvm", "2.2.0"))
    implementation(plugin("org.jetbrains.gradle.plugin.idea-ext", "1.2"))
    implementation(plugin("dev.isxander.modstitch.base", "0.7.0-unstable"))
    implementation(plugin("fabric-loom", "1.10.5"))
    implementation(plugin("net.neoforged.moddev", "2.0.80"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation(plugin( "org.cthing.build-constants","2.0.0"))
    implementation("net.minecraftforge.gradle","ForgeGradle", "6.0+")
    implementation(plugin("org.spongepowered.mixin", "0.7-SNAPSHOT"))
    implementation(plugin("org.jetbrains.gradle.plugin.idea-ext","1.2"))
    implementation(plugin("org.jetbrains.kotlin.jvm", "2.2.0"))
    implementation(plugin("com.gradleup.shadow","8.3.6"))
    implementation(plugin("com.gtnewhorizons.retrofuturagradle", "2.0.2"))

    //settings plugins
    implementation(plugin("org.danilopianini.gradle-pre-commit-git-hooks", "2.0.28"))
    implementation(plugin("org.gradle.toolchains.foojay-resolver-convention", "1.0.0"))
}
