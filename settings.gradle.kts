pluginManagement {
    includeBuild("convention-plugins")
    repositories {
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
            filter {
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


val apiProject = ":api"
val modProject = ":mod"

val supportedVersions = listOf(
//    "1.12.2",
    "1.21.4",
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
                    mc(it,loaders = listOf("forge"))
                else
                    mc(it, loaders = listOf("neoforge", "fabric"))
            }
        }
        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.4-neoforge"
    }
    create(apiProject) {
        with(apmc) {
            supportedVersions.forEach {
                mc(it,loaders = listOf("vanilla"))
            }
        }
        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.4-vanilla"
    }
}

rootProject.name = "ArchipelagoMinecraftClientCore"
