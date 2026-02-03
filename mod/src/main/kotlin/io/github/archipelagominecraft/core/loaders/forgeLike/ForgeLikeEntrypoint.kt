//? if forgeLike {
package io.github.archipelagominecraft.core.loaders.forgeLike


import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore

internal object ForgeLikeEntrypoint {
    fun fmlConstructMod() {
        ArchipelagoMinecraftClientCore.LOGGER.info("Registering Archipelago Location Types, sending event")
        ArchipelagoMinecraftClientCore.afterRegistration()
    }
}


//?}
