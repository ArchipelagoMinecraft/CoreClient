//? if forgeLike {
package io.github.archipelagominecraft.core.forgeLike


import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.api.ArchipelagoLocationType
import io.github.archipelagominecraft.core.compat.forgeLike.Event
import io.github.archipelagominecraft.core.compat.forgeLike.ForgeLike

class LocationTypeRegistrationEvent : Event(), ArchipelagoMinecraftClientCore.LocationTypeRegistrationManager {
    override fun registerLocationType(locationType: ArchipelagoLocationType<*, *>) {
        ArchipelagoMinecraftClientCore.registerLocationType(locationType)
    }
}

fun serverAboutToStart() {
    ArchipelagoMinecraftClientCore.LOGGER.info("Registering Archipelago Location Types, sending event")
    ForgeLike.EVENT_BUS.post(LocationTypeRegistrationEvent())
    ArchipelagoMinecraftClientCore.afterRegistration()
}
//?}
