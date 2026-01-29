
const val ENSURE_VCS_VERSION_TASK_NAME: String = "ensureVCSVersion"

object Keys {
    fun dep(name: String): String = "deps.$name"
    val parchmentMappingsVersion: String = dep("parchment")
    val fabricLoaderVersion: String = dep("fabricLoader")
    val neoforgeVersion: String = dep("neoforge")
    val neoformVersion: String = dep("neoform")
    val fabricApiVersion: String = dep("fabricApi")
    val mcpVersion: String = dep("mcp")
    val javaVersion: String = dep("java")
    val resourcePackFormat: String = dep("resource_pack_format")
    val dataPackFormat: String = dep("datapack_format")
    val minecraftVersion: String = dep("minecraft")
    val forgeVersion: String = dep("forge")

    val mcpChannel: String = dep("mappings.channel")
    val pluginType: String = dep("plugin_type")
}


enum class PluginTypes(val propValue: String) {
    RFG("retrofuturagradle"), // RetroFuturaGradle
    MODSTITCH("modstitch");
    companion object{
        fun parse(string: String): PluginTypes = entries.firstOrNull { it.propValue == string } ?: throw IllegalArgumentException("Unknown plugin type: $string")
    }
}

enum class ModLoaders(val propValue: String) {
    NONE_VANILLA("vanilla"),
    FORGE("forge"),
    FABRIC("fabric"),
    NEOFORGE("neoforge");
    companion object{
        fun parse(string: String): ModLoaders = entries.firstOrNull { it.propValue == string } ?: throw IllegalArgumentException("Unknown loader: $string")
    }
}
