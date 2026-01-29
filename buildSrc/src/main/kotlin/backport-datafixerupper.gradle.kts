import dev.kikugie.stonecutter.build.StonecutterBuildExtension

plugins {
    java
}

class DataFixerUpperExtension(
    val shouldBackport: Boolean,
    val dfuDependencies: List<Dependency>,
)


extensions.configure<StonecutterBuildExtension> {

    val stonecutter = this
    val shouldBackPort = stonecutter.current.parsed <= "1.12.2"
    if (shouldBackPort) {
        project.repositories {
            this.withType<MavenArtifactRepository>().named("mojang") {
                mavenContent {
                    excludeModule("com.mojang", "datafixerupper")
                }
            }
            maven("https://libraries.minecraft.net") {
                name = "datafixerupper-new"
                mavenContent {  //todo move exclusiveContent to this syntax
                    includeModule("com.mojang", "datafixerupper")
                }
                metadataSources {
                    artifact()
                }
            }
        }
    }
    extensions.add(
        "backportDfu",
        DataFixerUpperExtension(
            shouldBackPort,
            listOf(
                "com.mojang:datafixerupper:8.0.16",
                "it.unimi.dsi:fastutil:8.5.12"
            ).map {
                project.dependencies.create(it)
            }
        )
    )
}
