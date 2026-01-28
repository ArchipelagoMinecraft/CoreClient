//? if <=1.12.2 {
package io.github.archipelagominecraft.core.loaders.legacyforge

import io.github.archipelagominecraft.core.ArchipelagoClientConstants
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore.LOGGER
import io.github.archipelagominecraft.core.loaders.forgeLike.ForgeLikeEntrypoint
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent


@Mod(modid = ArchipelagoClientConstants.MOD_ID)
class LegacyForgeEntrypoint {
    @Mod.EventHandler
    fun preinit(preinit: FMLPreInitializationEvent) {
        LOGGER.info("Hello, world from 1.12.2!")
        ArchipelagoMinecraftClientCore.initialize()
    }

    @Mod.EventHandler
    fun init(evt: FMLServerAboutToStartEvent) {
        LOGGER.info("Legacy Forge Entrypoint initialized.")
        ForgeLikeEntrypoint.serverAboutToStart()
    }


} //?}

