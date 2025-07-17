package dev.isxander.controlify

import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType

internal val Project.modstitch: ModstitchExtension
    get() = extensions.getByType<ModstitchExtension>()

internal val Project.stonecutter: StonecutterBuildExtension
    get() = extensions.getByType<StonecutterBuildExtension>()

val Project.branchProj: Project
    get() = stonecutter.node.sibling("")!!.project

val Project.loader: String
    get() = stonecutter.current.project.reversed().split("-", limit = 2)[0].reversed()

val Project.isForge: Boolean
    get() = loader == "forge"
val Project.isNeoForge: Boolean
    get() = loader == "neoforge"
val Project.isFabric: Boolean
    get() = loader == "fabric"
val Project.isLegacy: Boolean
    get() = loader == "legacy"
val Project.isModern: Boolean
    get() = loader != "legacy"
val Project.isForgeLike: Boolean
    get() = isForge || isNeoForge

val Project.replacementProperties: Map<String, String>
    get() = buildMap {
        put("mod_id", requiredProp("mod.id"))
        put("mod_package", requiredProp("mod.package"))
        put("mod_name", requiredProp("mod.name"))
        put("mod_version", requiredProp("mod.version"))
        put("mod_license", requiredProp("mod.license"))
        put("mod_description", requiredProp("mod.description"))
        val authors = requiredProp("mod.authors")
        put("mod_authors", authors)
        put("mod_authors_json_list", Json.encodeToString(authors.split(",")))
        put("mod_issue_tracker", requiredProp("mod.issue_tracker"))
        val handler = if (isModern) {
            { it: String ->
                requiredProp(it)
            }
        } else {
            { it: String ->
                prop(it)
            }
        }
        val packFormat = handler("mod.pack_format") ?: ""
        put("mod_pack_format", packFormat)
    }


val Project.modVersion: String
    get() = prop("mod.version")!!
val Project.mcVersion: String
    get() = prop("deps.minecraft")!!

fun <T> Project.propMap(
    property: String,
    required: Boolean = false,
    ifNull: () -> String? = { null },
    block: (String) -> T?
): T? {
    return ((System.getenv(property) ?: branchProj.findProperty(property)?.toString())
        ?.takeUnless { it.isBlank() }
        ?: ifNull())
        .let { if (required && it == null) error("Property $property is required") else it }
        ?.let(block)
}

fun Project.requiredProp(property: String, ifNull: (() -> String)? = null) =
    propMap(property, required = true, ifNull = ifNull ?: { error("Property $property is required") }) { it }!!

fun Project.prop(property: String, required: Boolean = false, ifNull: () -> String? = { null }): String? {
    return propMap(property, required, ifNull) { it }
}

fun Project.isPropDefined(property: String): Boolean {
    return propMap(property) { true } == true
}

// Creates a global task that will only run on the active project
fun Project.createActiveTask(
    taskProvider: TaskProvider<*>? = null,
    taskName: String? = null,
    internal: Boolean = false
): String {
    val taskExists = taskProvider != null || taskName!! in tasks.names
    val task = taskProvider ?: taskName?.takeIf { taskExists }?.let { tasks.named(it) }
    val taskName = when {
        taskProvider != null -> taskProvider.name
        taskName != null -> taskName
        else -> error("Either taskProvider or taskName must be provided")
    }
    val activeTaskName = "${taskName}Active"

    if (stonecutter.current.isActive) {
        rootProject.tasks.register(activeTaskName) {
            group = "controlify${if (internal) "/versioned" else ""}"

            task?.let { dependsOn(it) }
        }
    }

    return activeTaskName
}

