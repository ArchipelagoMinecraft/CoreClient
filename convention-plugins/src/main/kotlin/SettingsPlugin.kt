import dev.kikugie.stonecutter.settings.StonecutterSettingsExtension
import dev.kikugie.stonecutter.settings.tree.TreeBuilder
import org.danilopianini.gradle.git.hooks.GitHooksExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType


@Suppress("unused")
abstract class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.plugins.apply {
            apply("dev.kikugie.stonecutter")
            apply("org.danilopianini.gradle-pre-commit-git-hooks")
            apply("org.gradle.toolchains.foojay-resolver-convention")
        }
        settings.extensions.configure<GitHooksExtension> {
            //todo find a cross-platform way
//            preCommit {
//                this@preCommit.from {
//                    """
//                ./gradlew -q $ENSURE_VCS_VERSION_TASK_NAME
//                if [ $? -ne 0 ]; then
//                    echo 'Stonecutter current version is not the VCS version!'
//                    echo 'Please run the "Reset active project" gradle task before committing.'
//                    exit 1
//                fi
//            """.trimIndent()
//                }
//            }
//            createHooks(true)
        }
        val stonecutter = settings.extensions.getByType<StonecutterSettingsExtension>()
        stonecutter.apply {
            kotlinController.set(true)
            centralScript.set("build.gradle.kts")
        }
        settings.extensions.add("apmc", APMCSettings)
    }
}

object APMCSettings {
    fun TreeBuilder.mc(
        mcVersion: String, vararg loaders: ModLoaders,
    ) {
        loaders.forEach { version("$mcVersion-${it.propValue}", mcVersion) }
    }
}
