//? if fabric {
/*package io.github.archipelagominecraft.core.loaders.fabric

import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore.LOGGER
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

class FabricEntrypoint : ModInitializer {
    public override fun onInitialize() {
        LOGGER.info("Hello from FabricEntrypoint!")
        ArchipelagoMinecraftClientCore.initialize()
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting {
            ArchipelagoMinecraftClientCore.afterRegistration()
        })
    }

} *///?}
