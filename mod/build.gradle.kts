import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import com.gtnewhorizons.retrofuturagradle.minecraft.RunMinecraftTask
import org.gradle.jvm.tasks.Jar
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

buildMultiversion {
    mixinsPackage = "io.github.archipelagominecraft.core.mixin"
}

val shade by configurations.registering
configurations.implementation {
    extendsFrom(shade.get())
}


dependencies {

    // we shade the kotlin stdlib in our jar (to not depend on things like KotlinForForge)
    // This means extra bloat if another mod provides kotlin, yes, but this also means that our mod is
    // completely standalone, and this is especially helpful on older minecraft versions
    shade(kotlin("stdlib-jdk8"))
    shade(project(":api:${modInfo.minecraftVersion}-vanilla"))
//    sha(project(":api:${modInfo.minecraftVersion}-vanilla"))

    if (backportDfu.shouldBackport) {
//        println(jvmdg.apiJar.get().map { it.absolutePath })
        downgradeImplementation(backportDfu.dfuDependency)
    }
    shade(project(":relocated-deps", configuration = "shadow"))
}

buildMultiversion {
    if (backportDfu.shouldBackport) {
        enableJvmDowngrader = true
    }
}

if(backportDfu.shouldBackport) {

    tasks.downgradeJar {
        inputFile = tasks.shadowJar.flatMap { it.archiveFile }
    }
    tasks.withType<ReobfuscatedJar>().named("reobfJar") {
        inputJar = tasks.downgradeJar.get().archiveFile
    }

    val paths = tasks.shadeDowngradedApi.get().archiveFile.get()
    tasks.withType<RunMinecraftTask>().configureEach {
//        dependsOn(tasks.shadeDowngradedApi)
        println("AAAAAAAAAAAAAAAAAAAAAAAAAAA")
        println(paths.asFile.absolutePath)
//        classpath = classpath
//            .minus(files(tasks.jar.get().archiveFile.get()))
//            .plus(files(tasks.shadeDowngradedApi.get().archiveFile.get()))


        // Make sure the downgraded jar actually exists before we try to add it.
        dependsOn(tasks.shadeDowngradedApi)

        doFirst {
            val downgraded = tasks.shadeDowngradedApi.get().archiveFile.get().asFile
            require(downgraded.exists()) { "Downgraded jar does not exist: ${downgraded.absolutePath}" }

            // Remove ANY non-downgraded variants of *this module* from the runtime classpath.
            // (Don't rely on tasks.jar only — RFG can put other outputs on the classpath.)
            val filtered = classpath.filter { f ->
                val n = f.name
                val looksLikeOurNonDowngraded =
                    n.contains("1.12.2-forge", ignoreCase = true) &&
                            n.endsWith(".jar", ignoreCase = true) &&
                            !n.contains("downgraded", ignoreCase = true)

                !looksLikeOurNonDowngraded
            }

            // Now force-add the downgraded jar (as a real File, eagerly)
            classpath = filtered.plus(files(downgraded))

            println("RunMinecraftTask classpath now contains downgraded jar: ${downgraded.name}")
        }

    }
}


val kotlinRelocateBase = "io.github.archipelagominecraft.core.shadow.kotlin"


// Rename output jars from
// XXX.jar -> non-shadowed
// XXX-all.jar -> shadowed
// to
// XXX-slim.jar -> non-shadowed
// XXX.jar -> shadowed
project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
//    archiveClassifier.set("slim")
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.WARN


//    archiveClassifier.set("")
    configurations = listOf(shade.get())
    if(backportDfu.shouldBackport) {
        exclude("module-info.class")
        exclude("META-INF/versions/**")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
//    configurations.set(shade.map { listOf(it) })
//    todo test conflict with other mods like kotlinForForge to see if relocation is really needed
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

