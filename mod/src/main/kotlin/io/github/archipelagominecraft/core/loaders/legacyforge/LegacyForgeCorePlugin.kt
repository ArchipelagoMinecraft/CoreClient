//? if <=1.12.2 {
/*package io.github.archipelagominecraft.core.loaders.legacyforge

import com.google.common.collect.ImmutableList
import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import io.github.archipelagominecraft.core.ArchipelagoClientConstants

// For MixinBooter
class LegacyForgeCorePlugin : IFMLLoadingPlugin, IEarlyMixinLoader {
    override fun getASMTransformerClass(): Array<String> = emptyArray()

    override fun getModContainerClass(): String? = null

    override fun getSetupClass(): String? = null

    override fun injectData(data: MutableMap<String, Any>) {}

    override fun getAccessTransformerClass(): String? = null

    override fun getMixinConfigs(): List<String?> =
        listOf(ArchipelagoClientConstants.MOD_MIXINS_FILE + ".mixins.json")
} *///?}

