package io.github.archipelagominecraft.core

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import io.github.archipelagominecraft.api.ArchipelagoMinecraftCoreRegistration
import io.github.archipelagominecraft.api.compat.Player
import java.io.File
import kotlin.jvm.optionals.getOrNull

const val DEFINITIONS_PATH = "../../../../definitions.json"


object ArchipelagoMinecraftClientCore {
    @JvmField
    internal val LOGGER: Logger = LogManager.getLogger(ArchipelagoClientConstants.MOD_NAME)


    @JvmStatic
    internal fun initialize() {
        LOGGER.info("Hello from ArchipelagoMinecraftClientCore!")
    }

    @JvmStatic
    internal fun afterRegistration() {
        val locationTypes = ArchipelagoMinecraftCoreRegistration.locationTypes
        LOGGER.info("All location types have been registered (${locationTypes.size}) : $locationTypes")
        val itemTypes = ArchipelagoMinecraftCoreRegistration.itemTypes
        LOGGER.info("All item types have been registered (${itemTypes.size}): $itemTypes")

        val defFile = File("").resolve(DEFINITIONS_PATH).canonicalFile
        val definitionsContent = defFile.readText()
        LOGGER.info("definitions path: ${defFile.path}")
        LOGGER.info("Definitions: $definitionsContent")


        // language=json
        val input = Gson().fromJson("""
    {
      "items": {
        "1": {
          "handlers": [
            {
              "type": "apsample:sample_type",
              "data": {
                "value": "example"
              }
            }
          ]
        }
      },
      "locations": {}
    }
        """.trimIndent(), JsonObject::class.java)
        val codec = ArchipelagoDefinitions.codec(
            mapOf(SampleType().let { it.id to it }),
            emptyMap()
        )
        val defs = ArchipelagoDefinitions(
            mapOf(
                1 to ArchipelagoItemDefinition(
                    listOf(
                        ArchipelagoItemHandler(
                            SampleType(),
                            SampleType.SampleData("example")
                        )
                    )
                )
            ),
            emptyMap()
        )
        val encoded = codec.encodeStart(JsonOps.INSTANCE, defs)
        LOGGER.info("Encoded defs: $encoded")
        val decoded = codec.decode(JsonOps.INSTANCE, input)

        val error = decoded.error().getOrNull()
        if (error != null) {
            LOGGER.error("Failed to decode definitions: ${error}")
            throw IllegalStateException("Failed to decode definitions file at ${defFile.path}: ${error}")
        }
        val definitions = decoded.result().get().first

        LOGGER.info("All definitions have been registered")
        LOGGER.info("Running with ${definitions.items.size} items and ${definitions.locations.size} locations.")

        definitions.locations.forEach { id, location ->
            val type = location.handler.type
            val data = location.handler.data
            LOGGER.info("Location $id is of type ${type.id} with data $data")
//            type.prepareLocation(null, data) //Todo after client connection
        }
        definitions.items.forEach { id, item ->
            item.handlers.forEach { handler ->
                val type = handler.type
                val data = handler.data
                LOGGER.info("Item $id is of type ${type.id} with data $data")
//                type.prepareItemForWorld(false, data) //Todo after client connection
            }
        }
    }


    internal fun synchronizeLocations(player: Player) {
        //todo
    }

    internal fun synchronizeItems(player: Player) {
        //todo
    }

    //todo link to event handlers
    internal fun onPlayerJoin(player: Player) {
        //todo synchronize items and locations for the player
        synchronizeLocations(player)
        synchronizeItems(player)
    }

}

