import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.modInfo

plugins {
    id("io.github.archipelagominecraft.build-multiversion-conventions")
}


dependencies {
    api(project(":api:${modInfo.minecraftVersion}-vanilla"))
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/ArchipelagoMinecraft/CoreClient")
            credentials {
                username = ""
                password = providers.gradleProperty("gpr.token").orElse(provider {
                    throw IllegalStateException("To get packages from github packages," +
                            " a Personal Access Token (PAT) is required, even for public packages, please create one" +
                            "and put it in \$GRADLE_USER_HOME/gradle.properties (NOT in the project's gradle.properties)")
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

