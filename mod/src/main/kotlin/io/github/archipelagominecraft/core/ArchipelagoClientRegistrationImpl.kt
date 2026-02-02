package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoMinecraftCoreRegistration
import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagominecraft.core.api.compat.Player
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationType

internal class ArchipelagoClientRegistrationImpl(
    override val serverManagedSlots: Set<ArchipelagoSlot>,
    private val getOnlinePlayerForSlotFunc: (ArchipelagoSlot) -> Set<Player>,
    private val getSlotRepresentingPlayerFunc: (Player) -> ArchipelagoSlot
) : ArchipelagoMinecraftCoreRegistration{
    internal val locationTypes: MutableMap<String, ArchipelagoLocationType<*>> = mutableMapOf()

    internal val itemTypes: MutableMap<String, ArchipelagoItemType<*>> = mutableMapOf()

    internal val randomizerFeatures: MutableMap<String, ArchipelagoRandomizerFeature<*>> = mutableMapOf()

    override fun getSlotRepresentingPlayer(player: Player): ArchipelagoSlot = getSlotRepresentingPlayerFunc(player)

    override fun getOnlinePlayersForSlot(slot: ArchipelagoSlot): Set<Player> = getOnlinePlayerForSlotFunc(slot)

    override fun registerLocationType(locationType: ArchipelagoLocationType<*>) {
        check(locationType.id !in locationTypes) { "A location type with the id ${locationType.id} is already registered" }
        locationTypes[locationType.id] = locationType
    }

    override fun registerItemType(itemType: ArchipelagoItemType<*>) {
        check(itemType.id !in itemTypes) { "An item type with the id ${itemType.id} is already registered" }
        itemTypes[itemType.id] = itemType
    }

    override fun registerRandomizerFeature(feature: ArchipelagoRandomizerFeature<*>) {
        check(feature.id !in randomizerFeatures) { "A randomizer feature with the id ${feature.id} is already registered" }
        randomizerFeatures[feature.id] = feature
    }
}
