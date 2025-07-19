plugins{
    `kotlin-dsl`
    kotlin("jvm") version "2.2.0"
}

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
    exclusiveContent {
        forRepositories(
            maven("https://maven.kikugie.dev/releases"),
            maven("https://maven.kikugie.dev/snapshots")
        )
        filter {
            includeGroupAndSubgroups("dev.kikugie")
        }
    }
}


dependencies{

    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"

    implementation(plugin("dev.isxander.modstitch.base", "0.5.16-unstable"))
    implementation(plugin("dev.kikugie.stonecutter", "0.7-beta.7"))
    implementation(plugin("fabric-loom", "1.10.5"))
    implementation(plugin("net.neoforged.moddev", "2.0.80"))
    implementation(plugin( "org.cthing.build-constants","2.0.0"))
    implementation("net.minecraftforge.gradle","ForgeGradle", "6.0+")
    implementation(plugin("org.spongepowered.mixin", "0.7-SNAPSHOT"))
    implementation(plugin("org.jetbrains.gradle.plugin.idea-ext","1.2"))
    implementation(plugin("com.gradleup.shadow","8.3.6"))
}
