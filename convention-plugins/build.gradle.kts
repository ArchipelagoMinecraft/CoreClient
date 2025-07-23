plugins{
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "2.2.0"
}

group = "io.github.archipelagominecraft"
version = "0.0.1-SNAPSHOT"

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
                password = System.getenv("GITHUB_TOKEN")
            }
        }

    }
}


dependencies{
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"

    implementation(plugin("dev.kikugie.j52j", "2.0"))
    implementation(plugin("dev.kikugie.stonecutter", "0.7-beta.7"))
    implementation(plugin("org.cthing.build-constants", "2.0.0"))
    implementation(plugin("me.modmuss50.mod-publish-plugin", "0.8.4"))
    implementation(plugin("org.jetbrains.kotlin.jvm", "2.2.0"))
    implementation(plugin("org.jetbrains.gradle.plugin.idea-ext", "1.2"))
    implementation(plugin("dev.isxander.modstitch.base", "0.5.16-unstable"))
    implementation(plugin("dev.kikugie.stonecutter", "0.7-beta.7"))
    implementation(plugin("fabric-loom", "1.10.5"))
    implementation(plugin("net.neoforged.moddev", "2.0.80"))
    implementation(plugin( "org.cthing.build-constants","2.0.0"))
    implementation("net.minecraftforge.gradle","ForgeGradle", "6.0+")
    implementation(plugin("org.spongepowered.mixin", "0.7-SNAPSHOT"))
    implementation(plugin("org.jetbrains.gradle.plugin.idea-ext","1.2"))
    implementation(plugin("org.jetbrains.kotlin.jvm", "2.2.0"))
    implementation(plugin("com.gradleup.shadow","8.3.6"))
    implementation(plugin("com.gtnewhorizons.retrofuturagradle", "1.4.6"))

    //settings plugins
    implementation(plugin("org.danilopianini.gradle-pre-commit-git-hooks", "2.0.28"))
    implementation(plugin("org.gradle.toolchains.foojay-resolver-convention", "1.0.0"))
}
