pluginManagement {
    includeBuild("convention-plugins")
    repositories {
//        maven("https://maven.pkg.github.com/ArchipelagoMinecraft/CoreClient")
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


stonecutter {
    create(rootProject) {
        with(apmc) {
            mc("1.12.2", loaders = listOf("legacy"))
            mc("1.21.4")
        mc("1.21.5")
        mc("1.21.6")
        mc("1.21.7")
        mc("1.21.8")
        }
        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.4-neoforge"
    }
}

rootProject.name = "ArchipelagoMinecraftClientCore"

