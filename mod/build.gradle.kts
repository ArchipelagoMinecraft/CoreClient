import io.github.archipelagominecraft.buildplugin.loader
import io.github.archipelagominecraft.buildplugin.modInfo

buildscript {
    dependencies {
        classpath("commons-io:commons-io:2.15.0")
    }
}
plugins {
    kotlin("jvm")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
    id("com.gradleup.shadow") version "9.3.1"
    `maven-publish`
}

group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xnested-type-aliases")
    }
    jvmToolchain {
        languageVersion = modstitch.javaVersion.map {
            JavaLanguageVersion.of(it)
        }
    }
}

val shade by configurations.registering

configurations.modstitchImplementation {
    extendsFrom(shade.get())
}

dependencies {
    shade(kotlin("stdlib-jdk8"))
    modstitch.retrofuturagradle {
        shade("com.mojang:datafixerupper:4.1.27")
    }
    shade("io.github.archipelagomw:Java-Client:0.1.20") {
        exclude("com.google.code.gson", "gson")
        // todo figure out if forcing it to use minecraft's older gson ( on legacy versions) will cause issues

    }
}

modstitch {
    retrofuturagradle {
        coreModClassName = "io.github.archipelagominecraft.core.loaders.legacyforge.LegacyForgeCorePlugin"
        hasModAndCoreMod = true
    }

    val file = project.parent!!.file("widener.aw")
    @Suppress("OPT_IN_USAGE")
    classTweaker.set(sc.process(file, "build/stonecutter_widener.aw"))
}

val kotlinRelocateBase = "io.github.archipelagominecraft.core.shadow.kotlin"

tasks.shadowJar {
    configurations = shade.map { setOf(it) }
    //todo figure out what to do about duplicate .kotlin_module files
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
                password = providers.gradleProperty("gpr.token").orNull
            }
        }
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
