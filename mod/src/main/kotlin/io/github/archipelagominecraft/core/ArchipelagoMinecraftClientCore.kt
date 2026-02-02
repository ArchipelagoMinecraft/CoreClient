package io.github.archipelagominecraft.core

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import io.github.archipelagominecraft.core.api.ArchipelagoMinecraftCoreRegistration
import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagominecraft.core.api.compat.Player
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemView
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationType
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationView
import io.github.archipelagominecraft.core.events.commands.APConnectCmd
import java.io.File
import kotlin.jvm.optionals.getOrNull

//? if fabric {
/*import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
*///?} elif neoforge {
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
//?}

//json5 to allow comments, Gson handles this
const val DEFINITIONS_PATH = "../../../../definitions.json5"

val ArchipelagoMinecraftCoreRegistration: ArchipelagoMinecraftCoreRegistration
    get() {
        return ArchipelagoMinecraftClientCore.registration
            ?: error("ArchipelagoMinecraftClientCore has not been initialized yet, try registering a bit later")
    }


internal object ArchipelagoMinecraftClientCore {
    @JvmField
    internal val LOGGER: Logger = LogManager.getLogger(ArchipelagoClientConstants.MOD_NAME)

    internal var registration: ArchipelagoClientRegistrationImpl? = null

    private val serverSlot: ArchipelagoSlot = ArchipelagoSlotImpl(true)

    /**
     * Called as soon as possible, when the mod is initialized
     */
    @JvmStatic
    internal fun initialize() {
        LOGGER.info("Hello from ArchipelagoMinecraftClientCore!")
        registration = ArchipelagoClientRegistrationImpl(
            setOf(serverSlot),
            { emptySet() },
            { serverSlot }
        )

        //? if fabric {
        /*CommandRegistrationCallback.EVENT.register({ dispatcher, registryAccess, environment ->
            APConnectCmd.connectCommand(dispatcher)
        })
        *///?}

        //todo for sample purposes, they should be in the "provider" mod
        ArchipelagoMinecraftCoreRegistration.registerItemType(SampleArchipelagoItemType)
        ArchipelagoMinecraftCoreRegistration.registerLocationType(SampleArchipelagoLocationType)
        ArchipelagoMinecraftCoreRegistration.registerRandomizerFeature(SampleArchipelagoFeatureWorldSeedRando)
    }

    //? if neoforge {
    @SubscribeEvent
    fun onRegisterCommandsEvent(event: RegisterCommandsEvent) {
        APConnectCmd.connectCommand(event.getDispatcher())
    } //?}

    /**
     * Called after provider mods had a chance to register their types
     */
    @JvmStatic
    internal fun afterRegistration() {
        val registration = registration ?: error("afterRegistration was called before initialize")
        val locationTypes = registration.locationTypes
        val itemTypes = registration.itemTypes
        val randomizerFeatures = registration.randomizerFeatures

        LOGGER.info("All location types have been registered (${locationTypes.size}) : $locationTypes")
        LOGGER.info("All item types have been registered (${itemTypes.size}): $itemTypes")
        LOGGER.info("All randomizer features have been registered: (${randomizerFeatures.size}): $randomizerFeatures")

        //get definitions file contents
        val defFile = File("").resolve(DEFINITIONS_PATH).canonicalFile
        val definitionsContent = defFile.readText()
        LOGGER.info("Using definitions file : ${defFile.path}")


        //parse definitions json and create the codecs
        val input = Gson().fromJson(definitionsContent, JsonElement::class.java)
        val codec = ArchipelagoDefinitions.codec(
            itemTypes,
            locationTypes,
        )

        val decoded = codec.decode(JsonOps.INSTANCE, input)

        val error = decoded.error().getOrNull()
        if (error != null) {
            LOGGER.error("Failed to decode definitions: $error")
            throw IllegalStateException("Failed to decode definitions file at ${defFile.path}: $error")
        }
        val definitions = decoded.result().get().first

        LOGGER.info("All definitions have been registered")
        LOGGER.info("Running with ${definitions.items.size} items and ${definitions.locations.size} locations.")


        val client: ArchipelagoContext = object : ArchipelagoContext {
            override val managedSlots: Set<ArchipelagoSlot> = setOf(serverSlot)

            override fun isLocationCheckedForSlot(
                slot: ArchipelagoSlot,
                location: APLocationID,
            ): Boolean = true

            override fun markLocationAsCheckedForSlot(
                slot: ArchipelagoSlot,
                location: APLocationID,
            ) {
            }

            override fun wasItemReceivedForSlot(
                slot: ArchipelagoSlot,
                item: APItemID,
            ) = true

            override fun registerItemListener(function: (id: APItemID, slot: ArchipelagoSlot) -> Unit) {

            }

            override fun getSlotData(
                key: String,
                slot: ArchipelagoSlot,
            ): String {
                return "42" // because the sample feature requires a number
            }

        }
        //end mock

        definitions.locations.forEach { (id, location) ->
            prepareLocationHandler(
                location.handler,
                ArchipelagoLocationImpl(id, client)
            )
            LOGGER.info("Location $id is of type ${location.handler.type.id} with data ${location.handler.data}")

        }
        definitions.items.forEach { (id, item) ->
            val itemView = ArchipelagoItemImpl(id, client)
            item.handlers.forEach { handler ->
                val type = handler.type
                val data = handler.data
                LOGGER.info("Item $id is of type ${type.id} with data $data")
                prepareItemHandler(
                    handler,
                    itemView
                )
            }
        }

        randomizerFeatures.values.forEach { feature ->
            prepareFeatureHandler(
                feature,
                client.managedSlots.associateWith {
                    client.getSlotData(feature.id, it)
                }
            )
        }


    }

    //utility function for generics
    private fun <T : ArchipelagoLocationType<D>, D> prepareLocationHandler(
        handler: ArchipelagoHandler<T, D>,
        locationView: ArchipelagoLocationView,
    ) {
        handler.type.prepareLocation(locationView, handler.data)
    }

    private fun <T : ArchipelagoItemType<D>, D> prepareItemHandler(
        handler: ArchipelagoHandler<T, D>,
        itemView: ArchipelagoItemView,
    ) {
        handler.type.prepareItem(itemView, handler.data)
    }

    private fun <D> prepareFeatureHandler(
        handler: ArchipelagoRandomizerFeature<D>,
        unserializedSlotData: Map<ArchipelagoSlot, String>,
    ) {
        val slotDatas = unserializedSlotData.mapValues { (_, value) ->
            val gsonParsed = Gson().fromJson(value, JsonElement::class.java)
            val decoded = handler.dataCodec.decode(JsonOps.INSTANCE, gsonParsed)
            decoded.getOrThrowCompat(false) { "Failed to parse slot data for feature: ${handler.id}, error : $it" }
                .first
        }
        handler.prepareFeature(
            ArchipelagoRandomizationFeatureImpl(
                slotDatas
            )
        )
    }


    @Suppress("unused")
    fun <R> DataResult<R>.getOrThrowCompat(ignored: Boolean, throwFun: (errorMessage: String) -> String): R {
//? if >1.12.2 {
        return this.getOrThrow {
            IllegalStateException(throwFun(it))
        }
//? } else {
        /*return this.getOrThrow(ignored,{throwFun(it)})
        *///? }
    }

    /**
     * For each location, check if the player has checked them or not in-game, and propagate this to
     * Archipelago
     */
    internal fun synchronizeLocations(player: Player) {
        //todo
    }

    /**
     * For each item the player has received on Archipelago, make sure that it was awared to the player already
     * and if not, award them now
     */
    internal fun synchronizeItems(player: Player) {
        //todo
    }

    //todo link to event handlers per-platform
    internal fun onPlayerJoin(player: Player) {
        //todo synchronize items and locations for the player
        synchronizeLocations(player)
        synchronizeItems(player)
    }

}

