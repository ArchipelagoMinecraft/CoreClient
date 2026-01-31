
import io.github.archipelagominecraft.buildplugin.ModLoaders
pluginManagement {
    includeBuild("multiVersionBuildPlugin")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.isxander.dev/releases/")
        maven("https://jitpack.io")
        exclusiveContent {
            forRepositories(
                maven("https://repo.spongepowered.org/maven")
            )
            filter {
                @Suppress("UnstableApiUsage")
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        exclusiveContent {
            // retrofuturagradle
            forRepositories(
                maven("https://nexus.gtnewhorizons.com/repository/public/")
            )
            @Suppress("UnstableApiUsage")
            filter {
                includeGroupAndSubgroups("com.gtnewhorizons")
                includeGroupAndSubgroups("com.gtnewhorizons.retrofuturagradle")
            }
        }
    }
}

plugins {
    id("io.github.archipelagominecraft.settings-conventions")
}


val modProject = ":mod"

val supportedVersions = listOf(
    "1.7.10",
    "1.12.2",
//    "1.21.4",
//    "1.21.5",
//    "1.21.6",
//    "1.21.7",
    "1.21.8"
)

stonecutter {
    create(modProject) {
        with(apmc) {
            supportedVersions.forEach {
                if (stonecutter.eval(it, "<=1.12.2"))
                    mc(it,ModLoaders.FORGE)
                else
                    mc(it, ModLoaders.NEOFORGE, ModLoaders.FABRIC)
            }
        }
        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.8-neoforge"
    }
}

rootProject.name = "ArchipelagoMinecraftClientCore"
