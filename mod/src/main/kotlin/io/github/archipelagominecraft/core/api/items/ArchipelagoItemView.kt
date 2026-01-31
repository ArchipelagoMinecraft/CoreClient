package io.github.archipelagominecraft.core.api.items

import io.github.archipelagominecraft.core.api.ArchipelagoMinecraftCoreRegistration
import io.github.archipelagominecraft.core.api.ArchipelagoSlot


/**
 * Represents a view of an Archipelago item
 */
@Suppress("unused")
interface ArchipelagoItemView {

    /**
     * Checks if an archipelago slot has already received the current item
     */
    fun hasSlotReceivedItem(slot: ArchipelagoSlot): Boolean

    /**
     * Allows registering a listener to be notified when a slot managed by this server receives this item
     */
    fun registerOnItemReceivedListener(listener: ArchipelagoItemReceivedListener)

    /**
     * Unregisters a listener
     */
    fun unregisterOnItemReceivedListener(listener: ArchipelagoItemReceivedListener)

}

fun interface ArchipelagoItemReceivedListener {
    /**
     * Called when the server is connected to Archipelago, and an item is received for the following slot.
     * Will be called even if no players are connected
     * @param slot The Archipelago slot that received the item.
     */
    fun onItemReceived(slot: ArchipelagoSlot)
}

/**
 * Checks if any slot managed by the server has already received the current item
 * This method makes sense for archipelago item types
 * that are not player-based, like allowing some mob to spawn for example
 */
fun ArchipelagoMinecraftCoreRegistration.hasAnySlotReceivedItem(itemView: ArchipelagoItemView): Boolean =
    serverManagedSlots.any {
        itemView.hasSlotReceivedItem(it)
    }



