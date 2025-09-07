import io.archipelagominecraft.gradle.*

plugins{
    id("io.github.archipelagominecraft.build-multiversion-conventions")
}


dependencies {
    implementation(project(":api:${modInfo.minecraftVersion}-vanilla"))
}

publishing {
    repositories {
//        maven {
//            url = uri("https://maven.pkg.github.com/ArchipelagoMinecraft/CoreClient")
//            credentials {
//                username = ""
//                password = providers.gradleProperty("gpr.token").get()
//            }
//        }
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

