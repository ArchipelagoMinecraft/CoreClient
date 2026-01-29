//? if <=1.12.2 {
/*package io.github.archipelagominecraft.core.loaders.legacyforge


import cpw.mods.fml.common.event.FMLConstructionEvent
import io.github.archipelagominecraft.core.ArchipelagoClientConstants
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore.LOGGER
import io.github.archipelagominecraft.core.loaders.forgeLike.ForgeLikeEntrypoint

//? if >= 1.12.2 {
typealias Mod = net.minecraftforge.fml.common.Mod
typealias EventHandler = net.minecraftforge.fml.common.Mod.EventHandler
typealias FMLPreInitializationEvent = net.minecraftforge.fml.common.event.FMLPreInitializationEvent
typealias FMLServerAboutToStartEvent = net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
//? } else {

/*typealias Mod = cpw.mods.fml.common.Mod
typealias EventHandler = cpw.mods.fml.common.Mod.EventHandler

typealias FMLPreInitializationEvent = cpw.mods.fml.common.event.FMLPreInitializationEvent
typealias FMLServerAboutToStartEvent = cpw.mods.fml.common.event.FMLServerAboutToStartEvent
*///? }

@Mod(modid = ArchipelagoClientConstants.MOD_ID)
class LegacyForgeEntrypoint {

    init {
        //? if = 1.12.2 {
        /*LOGGER.info("Hello, world from 1.12.2!")
        *///? } else if = 1.7.10 {
        /*LOGGER.info("Hello, world from 1.7.10!")
        *///? }
        ArchipelagoMinecraftClientCore.initialize()
    }
    @Suppress("unused")
    @EventHandler
    fun preinit(preinit: FMLPreInitializationEvent) {

    }

    @Suppress("unused")
    @EventHandler
    fun init(evt: FMLConstructionEvent) {
        LOGGER.info("Legacy Forge Entrypoint initialized.")
        ForgeLikeEntrypoint.fmlConstructMod()
    }


} *///?}

