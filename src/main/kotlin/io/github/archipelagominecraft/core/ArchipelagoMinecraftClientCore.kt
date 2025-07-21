package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoLocationType
import io.github.archipelagominecraft.core.compat.LogManager
import io.github.archipelagominecraft.core.compat.Logger
import io.github.archipelagominecraft.core.vanilla.ArchipelagoMinecraftVanilla

object ArchipelagoMinecraftClientCore {
    @JvmField
    internal val LOGGER: Logger = LogManager.getLogger(ArchipelagoClientConstants.MOD_NAME)

    private val LOCATION_TYPES: MutableMap<String, ArchipelagoLocationType<*, *>> =
        HashMap()

    @JvmStatic
    fun registerLocationType(locationType: ArchipelagoLocationType<*, *>) {
        check(!LOCATION_TYPES.containsKey(locationType.id)) { "Duplicated ArchipelagoLocationType: " + locationType.id }
        LOCATION_TYPES.put(locationType.id, locationType)
    }

    @JvmStatic
    internal fun initialize() {
        LOGGER.info("Hello from ArchipelagoMinecraftClientCore!")
        ArchipelagoMinecraftVanilla.initialize()
    }

    @JvmStatic
    internal fun afterRegistration() {
        LOGGER.info("All location types have been registered. Total: " + LOCATION_TYPES.size)
        LOCATION_TYPES.forEach { (id: String, type: ArchipelagoLocationType<*, *>) -> LOGGER.info("Registered location type: $id") }
        //parse apmcbundle
    }
}

