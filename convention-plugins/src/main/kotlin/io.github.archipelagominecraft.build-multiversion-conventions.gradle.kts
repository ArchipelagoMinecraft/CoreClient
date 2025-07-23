import dev.kikugie.j52j.J52JExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import io.archipelagominecraft.gradle.modInfo
import io.archipelagominecraft.gradle.serverWorkingDirectory
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

plugins {
    id("io.github.archipelagominecraft.conditional-modstitch")
    id("dev.kikugie.j52j")
    id("org.cthing.build-constants")
    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
}

val stonecutter = extensions.getByType<StonecutterBuildExtension>()
val j52j = extensions.getByType<J52JExtension>()

stonecutter.apply {
    filters{
        include("resources/**.json")
    }
    consts( //todo fix deprecated
        "fabric" to true,
        "neoforge" to true,
        "forge" to true,
        "forgeLike" to true,
    )
}
j52j.apply{
    params {
        prettyPrinting = true
    }
}



tasks.withType<KotlinCompile> {
    dependsOn(tasks.generateBuildConstants)
}


tasks.generateBuildConstants{
    classname.set(modInfo.packageName + "." + modInfo.name + "Constants")
    source(files("buildSrc/**"),files("versions/dependencies/**"))
    additionalConstants.put("MOD_ID", modInfo.id)
    additionalConstants.put("MOD_NAME", modInfo.name)
    additionalConstants.put("MOD_VERSION", modInfo.version)
    additionalConstants.put("MOD_MIXINS_FILE",modInfo.mixins ?: "")
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
