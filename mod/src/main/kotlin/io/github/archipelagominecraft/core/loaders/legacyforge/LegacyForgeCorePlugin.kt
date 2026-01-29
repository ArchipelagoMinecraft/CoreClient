//? if <=1.12.2 {
/*package io.github.archipelagominecraft.core.loaders.legacyforge

import io.github.archipelagominecraft.core.ArchipelagoClientConstants


//? if >= 1.8.9 {
import zone.rong.mixinbooter.IEarlyMixinLoader
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
//? } else {

/*import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader
import io.github.archipelagominecraft.core.mixins.ForceSeedMixinLegacy

*///? }

class LegacyForgeCorePlugin : IFMLLoadingPlugin, IEarlyMixinLoader {
    override fun getASMTransformerClass(): Array<String> = emptyArray()

    override fun getModContainerClass(): String? = null

    override fun getSetupClass(): String? = null

    override fun injectData(data: MutableMap<String, Any>) {}

    override fun getAccessTransformerClass(): String? = null

    val mixinConfigFile = ArchipelagoClientConstants.MOD_MIXINS_FILE_PREFIX + ".mixins.json"

    init {
        println("LEGACY FORGE CORE PLUGIN LOADED")
    }
//? if >= 1.8.9 {
    override fun getMixinConfigs(): List<String?> =
        listOf(mixinConfigFile)
    //?} else {
    /*override fun getMixinConfig(): String = mixinConfigFile
    override fun getMixins(loadedCoreMods: Set<String>): List<String> = listOf(
        ForceSeedMixinLegacy::class.simpleName!!
    )
    *///? }
}
*///?}

