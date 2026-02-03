package io.github.archipelagominecraft.buildplugin
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class EnsureVcsVersionTask : DefaultTask(){

    @get:Input
    abstract val vcsVersion: Property<String>

    @get:Input
    abstract val currentVersion: Property<String>

    init {
        group = "verification"
    }

    @TaskAction
    fun run(){

        val currentVersion = currentVersion.get()
        val vcsVersion = vcsVersion.get()
        if (currentVersion != vcsVersion) {
            throw GradleException("Current version ${currentVersion} is not the VCS version ${vcsVersion}!")
        }
    }
}
