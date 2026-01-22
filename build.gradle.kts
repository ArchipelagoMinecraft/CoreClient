import io.archipelagominecraft.gradle.*

plugins{
    id("conditional-modstitch")
    id("org.cthing.build-constants")
    alias(libs.plugins.j52j)
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

j52j{
    params {
        prettyPrinting = true
    }
}

tasks.generateBuildConstants{
    classname.set(modInfo.packageName + "." + modInfo.name + "Constants")
    source(files("buildSrc/**"),files("versions/dependencies/**"))
    additionalConstants.put("MOD_ID", modInfo.id)
    additionalConstants.put("MOD_NAME", modInfo.name)
    additionalConstants.put("MOD_VERSION", modInfo.version)
    additionalConstants.put("MOD_MIXINS_FILE",modInfo.mixins ?: "")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.github.archipelagomw:Java-Client:latest.integration")
}

stonecutter.apply {
    filters{
        include("resources/**.json")
    }
    consts( //todo fix deprecated
        "fabric" to isFabric,
        "neoforge" to isNeoForge,
        "forge" to isForge,
        "forgeLike" to isForgeLike,
        "legacy" to isLegacy,
        "modern" to isModern
    )
}


val acceptEula: TaskProvider<Task> by tasks.registering {
    doLast {
        val eulaFile = serverWorkingDirectory.file("eula.txt").asFile
        eulaFile.parentFile.mkdirs()
        eulaFile.createNewFile()
        eulaFile.writeText("eula=true")
    }
}

tasks.configureEach {
    if (this.name == "runServer") {
        dependsOn(acceptEula)
    }
}
