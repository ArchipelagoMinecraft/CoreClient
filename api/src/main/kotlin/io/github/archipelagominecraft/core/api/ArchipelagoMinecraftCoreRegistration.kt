package io.github.archipelagominecraft.core.api

import io.github.archipelagominecraft.core.api.compat.Player
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationType

object ArchipelagoMinecraftCoreRegistration {
    private val _locationTypes: MutableMap<String, ArchipelagoLocationType<*>> = mutableMapOf()
    val locationTypes: Map<String, ArchipelagoLocationType<*>> = _locationTypes

    private val _itemTypes: MutableMap<String, ArchipelagoItemType<*>> = mutableMapOf()
    val itemTypes: Map<String, ArchipelagoItemType<*>> = _itemTypes

    private val _randomizerFeatures: MutableMap<String, ArchipelagoRandomizerFeature<*>> = mutableMapOf()
    val randomizerFeatures: Map<String, ArchipelagoRandomizerFeature<*>> = _randomizerFeatures

    // provided by the mod, since they will use loader-specific stuff
    private lateinit var getSlotForPlayerFunc: (Player) -> ArchipelagoSlot
    private lateinit var getPlayersForSlotFunc: (ArchipelagoSlot) -> Set<Player>
    private lateinit var _serverManagedSlots: Set<ArchipelagoSlot>


    //todo find a better way to provide them, this function is public and that's bad, but I didn't find a better way for now
    fun internalRegisterPlayerSlots(
        getSlotForPlayerFunc: (Player) -> ArchipelagoSlot,
        getPlayersForSlotFunc: (ArchipelagoSlot) -> Set<Player>,
        serverManagedSlots: Set<ArchipelagoSlot>,
    ) {
        this.getSlotForPlayerFunc = getSlotForPlayerFunc
        this.getPlayersForSlotFunc = getPlayersForSlotFunc
        this._serverManagedSlots = serverManagedSlots
    }

    /**
     * Represents all the slots the server is managing
     */
    @JvmStatic
    val serverManagedSlots: Set<ArchipelagoSlot>
        get() = _serverManagedSlots

    /**
     * Returns the archipelago slot a player corresponds to
     * All players may return the same slot value if the server is playing as 1 single slot
     */
    @JvmStatic
    fun getSlotRepresentingPlayer(player: Player): ArchipelagoSlot = getSlotForPlayerFunc(player)

    /**
     * Returns the list of **currently-online** players that correspond to an Archipelago slot
     * The list could be all the currently-online players in case the server is playing as 1 single slot
     */
    @JvmStatic
    fun getOnlinePlayersForSlot(slot: ArchipelagoSlot): Set<Player> = getPlayersForSlotFunc(slot)


    @Suppress("unused")
    @JvmStatic
    fun registerLocationType(locationType: ArchipelagoLocationType<*>) {
        check(!_locationTypes.containsKey(locationType.id)) { "Duplicated ArchipelagoLocationType: " + locationType.id }
        _locationTypes[locationType.id] = locationType
    }

    @Suppress("unused")
    @JvmStatic
    fun registerItemType(itemType: ArchipelagoItemType<*>) {
        check(!_itemTypes.containsKey(itemType.id)) { "Duplicated ArchipelagoItemType: " + itemType.id }
        _itemTypes[itemType.id] = itemType
    }

    @Suppress("unused")
    @JvmStatic
    fun registerRandomizerFeature(feature: ArchipelagoRandomizerFeature<*>) {
        check(!_randomizerFeatures.containsKey(feature.id)) { "Duplicated ArchipelagoRandomizerFeature: " + feature.id }
        _randomizerFeatures[feature.id] = feature
    }


}
