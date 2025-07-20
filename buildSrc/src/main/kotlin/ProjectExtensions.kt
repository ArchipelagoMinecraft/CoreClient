package io.archipelagominecraft.gradle

import dev.isxander.modstitch.base.extensions.ModstitchExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

//taken from (end edited) : https://github.com/isXander/Controlify/blob/multiversion/dev/buildSrc/src/main/kotlin/dev/isxander/controlify/project.gradle.kts

data class ModInfo(
    val minecraftVersion: String,
    val id: String,
    val name: String,
    val packageName: String,
    val version: String,
    val license: String?,
    val homepage: String?,
    val description: String?,
    val authors: List<String>,
    val issueTracker: String?,
    val packFormat: String?,
    val mixins: String?,
    val javaVersion: Int,
    private val project: Project
) {

    fun requiredDep(name: String) = project.requiredProp("deps.$name")
}

val Project.modInfo: ModInfo
    get() = ModInfo(
        minecraftVersion = requiredProp("deps.minecraft"),
        id = requiredProp("mod.id"),
        name = requiredProp("mod.name"),
        packageName = requiredProp("mod.package"),
        javaVersion = requiredProp("deps.java").toInt(),
        version = requiredProp("mod.version"),
        packFormat = prop("mod.pack_format"),
        mixins = prop("mod.mixins"),
        license = prop("mod.license"),
        homepage = prop("mod.homepage"),
        description = prop("mod.description"),
        authors = requiredProp("mod.authors").split(",").map { it.trim() },
        issueTracker = prop("mod.issue_tracker"),
        project = this
    )

internal val Project.modstitch: ModstitchExtension
    get() = extensions.getByType<ModstitchExtension>()

internal val Project.stonecutter: StonecutterBuildExtension
    get() = extensions.getByType<StonecutterBuildExtension>()

val Project.branchProj: Project
    get() = stonecutter.node.sibling("")!!.project

val Project.loader: String
    get() = stonecutter.current.project.reversed().split("-", limit = 2)[0].reversed()

val Project.serverWorkingDirectory
    get() = layout.projectDirectory.dir(prop("run.server_working_directory") ?: "run")

val Project.clientWorkingDirectory
    get() = layout.projectDirectory.dir(prop("run.client_working_directory") ?: "run")

val Project.isForge: Boolean
    get() = loader == "forge" || loader == "legacy" //todo more generic
val Project.isNeoForge: Boolean
    get() = loader == "neoforge"
val Project.isFabric: Boolean
    get() = loader == "fabric"
val Project.isForgeLike: Boolean
    get() = isForge || isNeoForge


val Project.replacementProperties: Map<String, String>
    get() = buildMap {
        put("mod_id", modInfo.id)
        put("mod_package", requiredProp("mod.package"))
        put("mod_name", requiredProp("mod.name"))
        put("mod_version", requiredProp("mod.version"))
        put("mod_license", requiredProp("mod.license"))
        put("mod_homepage", requiredProp("mod.homepage"))
        put("mod_description", requiredProp("mod.description"))
        val authors = requiredProp("mod.authors")
        put("mod_authors", authors)
        put("mod_authors_json_list", Json.encodeToString(authors.split(",")))
        put("mod_issue_tracker", requiredProp("mod.issue_tracker"))
        val handler = if (stonecutter.eval(stonecutter.current.version,">=1.12.2")) {
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

fun Project.requiredProp(property: String, ifNull: (() -> String)? = null): String =
    propMap(property, required = true, ifNull = ifNull ?: { error("Property $property is required") }) { it }!!

fun Project.prop(property: String, required: Boolean = false, ifNull: () -> String? = { null }): String? {
    return propMap(property, required, ifNull) { it }
}

fun Project.isPropDefined(property: String): Boolean {
    return propMap(property) { true } == true
}
