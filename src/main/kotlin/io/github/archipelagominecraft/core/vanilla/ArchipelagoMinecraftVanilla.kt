package io.github.archipelagominecraft.core.vanilla

import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.api.ArchipelagoLocationType
//? if forgeLike {
import io.github.archipelagominecraft.core.compat.forgeLike.ForgeLike
import io.github.archipelagominecraft.core.compat.forgeLike.SubscribeEvent
import io.github.archipelagominecraft.core.forgeLike.LocationTypeRegistrationEvent

//?}


// pretend this is in the vanilla mod
object ArchipelagoMinecraftVanilla {
    var VANILLA_LOCATION_TYPES: List<ArchipelagoLocationType<*, *>> = listOf(
        AdvancementLocationType()
    )

    fun initialize() {
        ArchipelagoMinecraftClientCore.LOGGER.info(
            "Initializing ArchipelagoMinecraft Vanilla Entrypoint"
        )
        //? if forgeLike {
        ForgeLike.EVENT_BUS.register(object {
            @SubscribeEvent
            fun onEvent(evt: LocationTypeRegistrationEvent) =
                registerLocationTypes(evt)

        })
        //?} else if fabric {
        //?}
    }

    private fun registerLocationTypes(manager: ArchipelagoMinecraftClientCore.LocationTypeRegistrationManager) {
        ArchipelagoMinecraftClientCore.LOGGER.info("Registering Archipelago Location Types")
        VANILLA_LOCATION_TYPES.forEach { manager.registerLocationType(it) }
    }
}
