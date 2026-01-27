plugins {
    java
    id("com.gradleup.shadow")
}
/**
 * This project serves to relocate dependencies to prevent version clashes
 * Example: the archipelago java client depends on Gson, however minecraft already bundles Gson
 * We could exclude Gson from transitive dependencies, and force the java client to use minecraft's Gson
 * However this could result in version incompatibilities (especially on older versions)
 * So instead, we take everything that the java client needs (at their correct versions) and budle them, but relocated
 * to prevent name clashes
 * Again, this adds bloat, but serves to have a completely stand-alone mod
 *
 * Also, this needs to be a separate project because the projects requiring this needs to depend on the shadow variant
 * so that gradle can resolve dependencies correctly (because dependencies are resolved before shadowing/relocating)
 * Having this in a subproject effectively "hides" the transitive dependencies from the POV of the projects depending on
 * this
 */
group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"

repositories{
    mavenCentral()
}

dependencies {
    implementation("io.github.archipelagomw:Java-Client:0.1.20")
}

tasks.shadowJar {
    enableAutoRelocation = true
    relocationPrefix = "io.github.archipelagominecraft.core.shadow.deps"
}

