import kotlinx.serialization.json.Json

plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active file("versions/current")
tasks.register("printVersions") {
    val versions = stonecutter.versions.map { it.project }
    doLast {
        println("VERSIONS")
        println(Json.encodeToString(versions))
    }
}

fun activeTask(name: String): TaskProvider<Task> {
    return tasks.register(name + "Active") {
        group = "development"
        dependsOn(stonecutter.current!!.project + ":${name}")
    }
}

activeTask("runServer")
activeTask("runClient")
activeTask("build")
