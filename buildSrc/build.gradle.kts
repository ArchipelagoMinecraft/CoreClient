plugins{
    `kotlin-dsl`
    kotlin("jvm") version "2.2.0"
}

repositories{
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.minecraftforge.net/")
    exclusiveContent {
        forRepository {
            maven("https://maven.kikugie.dev/releases")
            maven("https://maven.kikugie.dev/snapshots")
        }
        filter {
            @Suppress("UnstableApiUsage")
            includeGroupAndSubgroups("dev.kikugie")
        }
    }
}



//todo use version catalogs maybe
dependencies{

    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"

    implementation(plugin("dev.isxander.modstitch.base", "0.5.16-unstable"))
    implementation(plugin("dev.kikugie.stonecutter", "0.7-beta.7"))
    implementation(plugin("fabric-loom", "1.10.5"))
    implementation(plugin("net.neoforged.moddev", "2.0.80"))
    implementation("net.minecraftforge.gradle","ForgeGradle", "6.0+")

}
