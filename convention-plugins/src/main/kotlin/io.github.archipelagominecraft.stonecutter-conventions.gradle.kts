import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import io.archipelagominecraft.gradle.loader
import kotlinx.serialization.json.Json
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}
val stonecutter = extensions.getByType<StonecutterControllerExtension>()
stonecutter active file("versions/current")

tasks.register("ensureVCSVersion") {
    doLast {
        val isVcsVersion = stonecutter.vcsVersion == stonecutter.current
        println("Current Version: ${stonecutter.current!!.version}")
        println("VCS Version: ${stonecutter.vcsVersion.version}")
        if(!isVcsVersion){
            throw GradleException("Current version is not the VCS version!")
        }
    }
}

if (idea.project != null) {
    idea.project.settings {
        runConfigurations {
            register<org.jetbrains.gradle.ext.Gradle>("Build") {
                this.taskNames = listOf("buildActive")
            }
            register<org.jetbrains.gradle.ext.Gradle>("Run Server") {
                this.taskNames = listOf("runServerActive")
            }
            register<org.jetbrains.gradle.ext.Gradle>("Run Client") {
                this.taskNames = listOf("runClientActive")
            }
            register<org.jetbrains.gradle.ext.Gradle>("Restore VCS Version") {
                this.taskNames = listOf("'Reset active project'")
            }
            register<org.jetbrains.gradle.ext.Gradle>("Refresh Stonecutter comments") {
                this.taskNames = listOf("'Refresh active project'")
            }
        }
    }
}

tasks.register("printVersions") {
    val versions = stonecutter.versions.map { it.project }
    doLast {
        println("VERSIONS")
        println(Json.encodeToString(versions))
    }
}

fun activeTask(name: String): TaskProvider<Task> {
    return tasks.register(name + "Active") {
        group = "development"
        dependsOn(stonecutter.current!!.project + ":${name}")
    }
}
if (stonecutter.current?.project?.split("-", limit = 2)?.get(1) != LoaderConstants.VANILLA) {
    activeTask("runServer")
    activeTask("runClient")
}
activeTask("build")
