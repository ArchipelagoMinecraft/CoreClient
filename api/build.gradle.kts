plugins {
    id("dev.kikugie.stonecutter")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
}

group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"

repositories{
    mavenCentral()
}

dependencies {
    implementation("io.github.archipelagomw:Java-Client:0.1.20") {
        exclude(group= "com.google.code.gson", module = "gson")
    }
}


tasks.generateBuildConstants {
    enabled = false
}
