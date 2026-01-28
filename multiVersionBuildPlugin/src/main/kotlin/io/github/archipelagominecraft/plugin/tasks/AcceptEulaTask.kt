package io.github.archipelagominecraft.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

//abstract class AcceptEulaTask : DefaultTask() {
//
//    @get:Input
//    abstract val serverDirectory: DirectoryProperty
//
//    @get:OutputFile
//    abstract val eulaFile: RegularFileProperty
//
//    init {
//        group = "other"
//        description = "Creates an eula.txt file in the server directory."
//        eulaFile.convention(serverDirectory.get().file("eula.txt"))
//    }
//
//    @TaskAction
//    fun run() {
//        eulaFile.get().asFile.writeText("eula=true")
//    }
//}
