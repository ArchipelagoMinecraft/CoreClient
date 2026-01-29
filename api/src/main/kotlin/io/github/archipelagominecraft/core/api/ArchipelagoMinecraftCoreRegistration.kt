package io.github.archipelagominecraft.core.api

import io.github.archipelagominecraft.core.api.compat.Player
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationType

interface ArchipelagoMinecraftCoreRegistration {


    /**
     * Represents all the slots the server is managing
     */
    val serverManagedSlots: Set<ArchipelagoSlot>

    /**
     * Returns the archipelago slot a player corresponds to
     * All players may return the same slot value if the server is playing as 1 single slot
     */
    fun getSlotRepresentingPlayer(player: Player): ArchipelagoSlot

    /**
     * Returns the list of **currently-online** players that correspond to an Archipelago slot
     * The list could be all the currently-online players in case the server is playing as 1 single slot
     */
    fun getOnlinePlayersForSlot(slot: ArchipelagoSlot): Set<Player>

    fun registerLocationType(locationType: ArchipelagoLocationType<*>)

    fun registerItemType(itemType: ArchipelagoItemType<*>)

    fun registerRandomizerFeature(feature: ArchipelagoRandomizerFeature<*>)

}
