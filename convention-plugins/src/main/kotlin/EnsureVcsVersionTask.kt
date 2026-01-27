import dev.kikugie.stonecutter.settings.StonecutterSettingsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.work.DisableCachingByDefault

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
        println("Current Version: $currentVersion")
        val vcsVersion = vcsVersion.get()
        println("VCS Version: $vcsVersion")
        if (currentVersion != vcsVersion) {
            throw GradleException("Current version is not the VCS version!")
        }
    }
}
