package io.archipelagominecraft.gradle

import Keys
import ModLoaders
import PluginTypes
import defaultProperties
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType


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
    val resourcePackFormat: Int?,
    val dataPackFormat: Int?,
    val mixinsFilePrefix: String?,
    val javaVersion: Int,
    private val project: Project,
)

val Project.modInfo: ModInfo
    get() = ModInfo(
        minecraftVersion = requiredProp(Keys.minecraftVersion),
        id = requiredProp("mod.id"),
        name = requiredProp("mod.name"),
        packageName = requiredProp("mod.package"),
        version = requiredProp("mod.version"),
        license = prop("mod.license"),
        homepage = prop("mod.homepage"),
        description = prop("mod.description"),
        authors = requiredProp("mod.authors").split(",").map { it.trim() },
        issueTracker = prop("mod.issue_tracker"),
        resourcePackFormat = prop(Keys.resourcePackFormat)?.toInt(),
        dataPackFormat = prop(Keys.dataPackFormat)?.toInt(),
        mixinsFilePrefix = prop("mod.mixins_file_prefix"),
        javaVersion = requiredProp(Keys.javaVersion).toInt(),
        project = this,
    )

internal val Project.stonecutter: StonecutterBuildExtension
    get() = extensions.getByType<StonecutterBuildExtension>()

val Project.loader: ModLoaders
    get() = stonecutter.current.project.reversed().split("-", limit = 2)[0].reversed()
        .let(ModLoaders::parse)
val Project.pluginType: PluginTypes
    get() = requiredProp(Keys.pluginType).let(PluginTypes::parse)

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

        if (stonecutter.eval(stonecutter.current.version, ">=1.6.1")) {
            val value =
                requireNotNull(modInfo.resourcePackFormat) { "Resource pack format is required for versions starting with 1.6.1" }
            put("resource_pack_format", value.toString())
        }
        if (stonecutter.eval(stonecutter.current.version, ">=1.13")) {
            val value =
                requireNotNull(modInfo.dataPackFormat) { "Data pack format is required for versions starting with 1.13" }
            put("data_pack_format", value.toString())
        }
        modInfo.mixinsFilePrefix?.let { put("mixins_file_prefix", it) }
        put("mixin_compat_level", "JAVA_${modInfo.javaVersion}")
    }


fun Project.prop(
    property: String,
): String? {
    project.findProperty(property) ?: defaultProperties[stonecutter.current.version]?.get(property)

    val versionedProps = defaultProperties[stonecutter.current.version]
    return project.findProperty(property)?.toString()
        ?: versionedProps?.get(property)
}

fun Project.requiredProp(property: String): String = prop(property) ?: error("The property $property is required")
