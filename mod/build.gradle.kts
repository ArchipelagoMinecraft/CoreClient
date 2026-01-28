import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo

plugins {
    kotlin("jvm")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
    id("backport-datafixerupper")
    id("com.gradleup.shadow")
    `maven-publish`
}

group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xnested-type-aliases")
    }
}

val shade by configurations.registering
configurations.implementation {
    extendsFrom(shade.get())
}

dependencies {
    shade(kotlin("stdlib-jdk8"))
    shade(project(":api:${modInfo.minecraftVersion}-vanilla"))

    if (backportDfu.shouldBackport) {
        downgradeImplementation(backportDfu.dfuDependency)
    }
    shade(project(":relocated-deps", configuration = "shadow"))
}

buildMultiversion {
    if (backportDfu.shouldBackport) {
        enableJvmDowngrader = true
    }
}

if (backportDfu.shouldBackport) {
    tasks.downgradeJar {
        inputFile = tasks.shadowJar.flatMap { it.archiveFile }
    }
}

val kotlinRelocateBase = "io.github.archipelagominecraft.core.shadow.kotlin"

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
    configurations = listOf(shade.get())
    if (backportDfu.shouldBackport) {
        exclude("module-info.class")
        exclude("META-INF/versions/**")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
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
                password = providers.gradleProperty("gpr.token").orElse(provider {
                    throw IllegalStateException(
                        "To get packages from github packages," +
                                " a Personal Access Token (PAT) is required, even for public packages, please create one" +
                                "and put it in \$GRADLE_USER_HOME/gradle.properties (NOT in the project's gradle.properties)"
                    )
                }).get()
            }
        }
        mavenLocal()
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
