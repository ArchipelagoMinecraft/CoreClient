import dev.kikugie.stonecutter.data.tree.TreeBuilder
import dev.kikugie.stonecutter.settings.StonecutterSettingsExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.gitHooks

abstract class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.plugins.apply {
            apply("dev.kikugie.stonecutter")
            apply("org.danilopianini.gradle-pre-commit-git-hooks")
            apply("org.gradle.toolchains.foojay-resolver-convention")
        }

        settings.gradle.settingsEvaluated {
            gitHooks {
                preCommit {
                    this@preCommit.from {
                        """
                ./gradlew -q ensureVCSVersion >/dev/null 2>&1
                if [ $? -ne 0 ]; then
                    echo 'Stonecutter current version is not the VCS version!'
                    echo 'Please run the "Reset active project" gradle task before committing.'
                    exit 1
                fi
            """.trimIndent()
                    }
                }

                createHooks(true)
            }

            val stonecutter = settings.extensions.getByType<StonecutterSettingsExtension>()
            stonecutter.apply {
                kotlinController.set(true)
                centralScript.set("build.gradle.kts")
            }
        }
        settings.extensions.add("apmc", APMCSettings)
    }
}

object APMCSettings  {
    fun TreeBuilder.mc(
        mcVersion: String, name: String = mcVersion, loaders: Iterable<String>
        = listOf("fabric", "neoforge", "vanilla")
    ) {
        loaders.forEach { vers("$name-$it", mcVersion) }
    }
}
