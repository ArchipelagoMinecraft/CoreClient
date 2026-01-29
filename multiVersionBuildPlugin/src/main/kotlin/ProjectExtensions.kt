package io.archipelagominecraft.gradle

import Keys
import ModLoaders
import PluginTypes
import defaultProperties
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.provider.Provider
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
        minecraftVersion = requiredProp(Keys.minecraftVersion).get(),
        id = requiredProp("mod.id").get(),
        name = requiredProp("mod.name").get(),
        packageName = requiredProp("mod.package").get(),
        version = requiredProp("mod.version").get(),
        license = prop("mod.license").get(),
        homepage = prop("mod.homepage").get(),
        description = prop("mod.description").get(),
        authors = requiredProp("mod.authors").get().split(",").map { it.trim() },
        issueTracker = prop("mod.issue_tracker").get(),
        resourcePackFormat = prop(Keys.resourcePackFormat).get()?.toInt(),
        dataPackFormat = prop(Keys.dataPackFormat).get()?.toInt(),
        mixinsFilePrefix = prop("mod.mixins_file_prefix").get(),
        javaVersion = requiredProp(Keys.javaVersion).get().toInt(),
        project = this,
    )

internal val Project.stonecutter: StonecutterBuildExtension
    get() = extensions.getByType<StonecutterBuildExtension>()

val Project.loader: ModLoaders
    get() = stonecutter.current.project.reversed().split("-", limit = 2)[0].reversed()
        .let(ModLoaders::parse)

val Project.replacementProperties: Map<String, String>
    get() = buildMap {
        put("mod_id", modInfo.id)
        put("mod_package", requiredProp("mod.package").get())
        put("mod_name", requiredProp("mod.name").get())
        put("mod_version", requiredProp("mod.version").get())
        put("mod_license", requiredProp("mod.license").get())
        put("mod_homepage", requiredProp("mod.homepage").get())
        put("mod_description", requiredProp("mod.description").get())
        val authors = requiredProp("mod.authors").get()
        put("mod_authors", authors)
        put("mod_authors_json_list", Json.encodeToString(authors.split(",")))
        put("mod_issue_tracker", requiredProp("mod.issue_tracker").get())

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
): Provider<String> {
    project.findProperty(property) ?: defaultProperties[stonecutter.current.version]?.get(property)

    val versionedProps = defaultProperties[stonecutter.current.version]
    val findProvider = provider {project.findProperty(property) as String?}
    val defaultProvider = provider {versionedProps?.get(property)}
    return findProvider.orElse(defaultProvider)
}

fun Project.requiredProp(property: String): Provider<String> = prop(property)
