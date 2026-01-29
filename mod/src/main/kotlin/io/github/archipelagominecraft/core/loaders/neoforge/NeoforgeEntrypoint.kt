//? if neoforge {
package io.github.archipelagominecraft.core.loaders.neoforge

import io.github.archipelagominecraft.core.ArchipelagoClientConstants
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore.LOGGER
import io.github.archipelagominecraft.core.loaders.forgeLike.ForgeLikeEntrypoint
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent

@Mod(ArchipelagoClientConstants.MOD_ID)
class NeoforgeEntrypoint(modBus: IEventBus) {
    init {
        LOGGER.info("Hello from NeoforgeEntrypoint!")
        ArchipelagoMinecraftClientCore.initialize()
        modBus.addListener<FMLConstructModEvent> { e -> ForgeLikeEntrypoint.fmlConstructMod() }
    }

} //?}
