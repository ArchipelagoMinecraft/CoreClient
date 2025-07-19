pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Modstitch
        maven("https://maven.isxander.dev/releases/")

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
        maven("https://maven.neoforged.net/releases/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        // Modstitch
        maven("https://maven.isxander.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-beta.7"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.28"
}
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

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        /**
         * @param mcVersion The base minecraft version.
         * @param loaders A list of loaders to target, supports "fabric" (1.14+), "neoforge"(1.20.6+), "vanilla"(any) or "forge"(<=1.20.1)
         */
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
            loaders.forEach { vers("$name-$it", mcVersion) }

        // Configure your targets here!
        mc("1.12.2", loaders = listOf("legacy"))
        mc("1.21.4", loaders = listOf("fabric","neoforge","vanilla"))
//        mc("1.20.1", loaders = listOf("forge"))

        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.4-neoforge"
    }
}

rootProject.name = "ArchipelagoMinecraftClientCore"

