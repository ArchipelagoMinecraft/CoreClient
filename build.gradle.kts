import io.archipelagominecraft.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins{
    id("conditional-modstitch")
    id("org.cthing.build-constants")
    alias(libs.plugins.j52j)
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.generateBuildConstants)
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

stonecutter.apply {
    filters{
        include("resources/**.json")
    }
    consts( //todo fix deprecated
        "fabric" to isFabric,
        "neoforge" to isNeoForge,
        "forge" to isForge,
        "forgeLike" to isForgeLike,
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
