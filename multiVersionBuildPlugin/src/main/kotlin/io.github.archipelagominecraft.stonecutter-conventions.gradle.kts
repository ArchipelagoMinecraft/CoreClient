import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import io.github.archipelagominecraft.buildplugin.ENSURE_VCS_VERSION_TASK_NAME
import io.github.archipelagominecraft.buildplugin.EnsureVcsVersionTask
import io.github.archipelagominecraft.buildplugin.ModLoaders
import kotlinx.serialization.json.Json
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}
val stonecutter = extensions.getByType<StonecutterControllerExtension>()
stonecutter active file("versions/current")

// Used for the git hook, to ensure every commit uses the defined VCS version
tasks.register<EnsureVcsVersionTask>(ENSURE_VCS_VERSION_TASK_NAME) {
    vcsVersion = stonecutter.vcsVersion.version
    currentVersion = stonecutter.current!!.version
}

// Creates some tasks in IntelliJ
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

/**
 * Prints a JSON array of versions declared by stonecutter
 * Used for CI
 */
tasks.register("printVersions") {
    val versions = stonecutter.versions.map { it.project }
    doLast {
        println("VERSIONS")
        println(Json.encodeToString(versions))
    }
}

/**
 * Creates a gradle task that will only run on the current active stonecutter subproject
 * Because if you just run a task on the root project it will run it on *all* subprojects
 */
fun activeTask(name: String): TaskProvider<Task> {
    return tasks.register(name + "Active") {
        group = "development"
        dependsOn(stonecutter.current!!.project + ":${name}")
    }
}
if (stonecutter.current?.project?.split("-", limit = 2)?.get(1) != ModLoaders.NONE_VANILLA.propValue) {
    activeTask("runServer")

    activeTask("runClient")
}
