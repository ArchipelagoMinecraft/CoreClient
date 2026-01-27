import com.github.jengelman.gradle.plugins.shadow.ShadowBasePlugin.Companion.shadow
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import io.archipelagominecraft.gradle.pluginType

plugins {
    kotlin("jvm")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
    id("com.gradleup.shadow")
}

group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"

val shade by configurations.registering

val wantsDfu = stonecutter.current.parsed <= "1.12.2"
repositories {
    if(wantsDfu) {
        exclusiveContent {
            forRepository {
                maven("https://libraries.minecraft.net")
            }
            filter {
                includeModule("com.mojang", "datafixerupper")
            }
        }
    }
}
dependencies {
    if(wantsDfu) {
        val dfu = "com.mojang:datafixerupper:8.0.16"
        shade(dfu)
        api(dfu)
    }
}
configurations.runtimeElements {
    attributes {
        attribute(
            ModUtils.DEOBFUSCATOR_TRANSFORMED,
            true
        )
    }
}//todo remove

buildMultiversion {
    enableJvmDowngrader = false
}



configurations.implementation {
    extendsFrom(shade.get())
}
tasks.shadowJar {
    configurations = listOf(shade.get())
}

repositories{
    mavenCentral()
}


tasks.generateBuildConstants {
    enabled = false
}
