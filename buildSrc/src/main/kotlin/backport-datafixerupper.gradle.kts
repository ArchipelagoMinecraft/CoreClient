import dev.kikugie.stonecutter.build.StonecutterBuildExtension

plugins {
    java
}

class DataFixerUpperExtension(
    val shouldBackport: Boolean,
    val dfuDependency: Dependency,
)

//configurations {
//    downgrade
//    implementation.extendsFrom downgrade
//}
//
//jvmdg.dg(configurations.downgrade) {
//    downgradeTo = JavaVersion.VERSION_1_8 // default
//}
//
//dependencies {
//    downgrade "newer.java:version:1.0"
//}

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
//                    mavenPom()
                    artifact()
                }
            }
        }
    }
    extensions.add(
        "backportDfu",
        DataFixerUpperExtension(
            shouldBackPort,
            project.dependencies.create("com.mojang:datafixerupper:8.0.16")
        )
    )
}
