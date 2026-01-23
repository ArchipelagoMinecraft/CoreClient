package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoSlot

interface ArchipelagoClient {
    val managedSlots: Set<ArchipelagoSlot>
    fun isLocationCheckedForSlot(slot: ArchipelagoSlot, location: APLocationID): Boolean
    fun markLocationAsCheckedForSlot(slot: ArchipelagoSlot, location: APLocationID)
    fun wasItemReceivedForSlot(slot: ArchipelagoSlot, item: APItemID): Boolean

    fun registerItemListener(function: (id: APItemID, slot: ArchipelagoSlot) -> Unit)
    fun getSlotData(key: String, slot: ArchipelagoSlot): String
}
