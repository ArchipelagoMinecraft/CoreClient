object Keys {
    fun dep(name: String): String = "deps.$name"
    val fabricLoaderVersion: String = dep("fabricLoader")
    val neoforgeVersion: String = dep("neoforge")
    val neoformVersion: String = dep("neoform")
    val fabricApiVersion: String = dep("fabricApi")
    val mcpVersion: String = dep("mcp")
    val vanillaMixinsVersion: String = dep("vanilla.mixins")
    val mixinsFileName = dep("mixins_file_name")
    val javaVersion: String = dep("java")
    val resourcePackFormat: String = dep("resource_pack_format")
    val dataPackFormat: String = dep("datapack_format")
    val minecraftVersion: String = dep("minecraft")
    val forgeVersion: String = dep("forge")

    val dfuVersion: String = dep("datafixerupper")

    val mappingsChannel: String = dep("mappings.channel")
    val mappingsVersion: String = dep("mappings.version")

    val fmlCorePluginClass: String = "mod.fml_core_plugin_class"
}

object LoaderConstants {
    //uses retrofuturagradle
    const val LEGACY = "legacy"

    // uses modstitch
    const val VANILLA = "vanilla"
    const val FORGE = "forge"
    const val FABRIC = "fabric"
    const val NEOFORGE = "neoforge"
}
