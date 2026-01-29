import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo
import io.github.archipelagominecraft.plugin.configs.retroFuturaGradle

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
    jvmToolchain(modstitch.javaVersion.get())
}

val shade by configurations.registering
configurations.implementation {
    extendsFrom(shade.get())
}
val shadeDowngrade = if(backportDfu.shouldBackport) {
    val shadeDowngrade = configurations.register("shadeDowngrade")
    configurations.named("modstitchDowngradeImplementation") {
        extendsFrom(shadeDowngrade.get())
    }
} else null

dependencies {
    shade(kotlin("stdlib-jdk8"))
    shade(project(":api:${modInfo.minecraftVersion}-vanilla"))

    if (backportDfu.shouldBackport) {
        backportDfu.dfuDependencies.forEach {
            shadeDowngrade?.invoke(it)
        }
    }
    shade(project(":relocated-deps", configuration = "shadow"))
}

modstitch {
    retroFuturaGradle {
        if(backportDfu.shouldBackport){
            enableJvmDowngrader = true
            jvmDowngraderDowngradeJarTask {
                inputFile.set(tasks.named("reobfJar", ReobfuscatedJar::class.java).flatMap { it.archiveFile })
            }
        }
        tasks.named("reobfJar", ReobfuscatedJar::class.java) {
            inputJar.set(tasks.shadowJar.flatMap { it.archiveFile })
        }
        coreModClassName = "io.github.archipelagominecraft.core.loaders.legacyforge.LegacyForgeCorePlugin"
        hasModAndCoreMod = true
    }

    val file = project.parent!!.file("accesstransformer.cfg")
    @Suppress("OPT_IN_USAGE")
    classTweaker.set(sc.process(file,"build/stonecutter_accesstransformer.cfg"))
}

val kotlinRelocateBase = "io.github.archipelagominecraft.core.shadow.kotlin"



tasks.shadowJar {
    configurations.set(emptyList())
    configurations = listOf(shade.get()) + shadeDowngrade?.let{listOf(it.get())}.orEmpty()
    if (backportDfu.shouldBackport) {
        exclude("module-info.class")
        exclude("META-INF/versions/**")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    modstitch {
        retroFuturaGradle {
            dependencies {
                // Because the "api" project depends on mixins, the shadow plugin would shade it if
                // we didn't specify this
                exclude {
                    val full = it.moduleGroup + ":" + it.moduleName + ":" + it.moduleVersion
                    this@retroFuturaGradle.mixinsDependencies.get().contains(full)
                }
            }
        }
    }
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
