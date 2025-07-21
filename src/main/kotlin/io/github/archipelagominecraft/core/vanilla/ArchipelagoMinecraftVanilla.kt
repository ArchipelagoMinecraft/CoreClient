package io.github.archipelagominecraft.core.vanilla

import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore
import io.github.archipelagominecraft.core.api.ArchipelagoLocationType

// pretend this is in the vanilla mod
object ArchipelagoMinecraftVanilla {
    var VANILLA_LOCATION_TYPES: List<ArchipelagoVanillaLocation<*, *>> = listOf(
        AdvancementLocationType()
    )

    fun initialize() {
        ArchipelagoMinecraftClientCore.LOGGER.info(
            "Initializing ArchipelagoMinecraft Vanilla Entrypoint"
        )
        VANILLA_LOCATION_TYPES.forEach {
            it.registerListeners()
            ArchipelagoMinecraftClientCore.registerLocationType(it)
        }
    }
}

abstract class ArchipelagoVanillaLocation<S: ArchipelagoVanillaLocation<S,T>,T> : ArchipelagoLocationType<S, T> {
    abstract fun registerListeners()
}
