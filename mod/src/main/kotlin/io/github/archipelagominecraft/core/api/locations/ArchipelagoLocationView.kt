package io.github.archipelagominecraft.core.api.locations

import io.github.archipelagominecraft.core.api.ArchipelagoMinecraftCoreRegistration
import io.github.archipelagominecraft.core.api.ArchipelagoSlot


/**
 * Represents a view of an Archipelago location that can be checked for a specific player.
 *
 * This interface is used to determine if an archipelago slot has checked a specific location and
 * to mark the location as checked in-game if it was already checked (for example show an advancement as unlocked)
 *
 */
@Suppress("unused")
interface ArchipelagoLocationView {
    /**
     * Marks this location as checked for the slots present in the `slots` list
     */
    fun checkFor(slots: List<ArchipelagoSlot>)

    /**
     * Returns if the location has been checked by the following slot
     */
    fun isCheckedFor(slot: ArchipelagoSlot): Boolean


}

/**
 * Returns if the location has been checked for any slot the server is responsible for
 */
fun ArchipelagoMinecraftCoreRegistration.isCheckedForAnySlot(locationView: ArchipelagoLocationView): Boolean =
    serverManagedSlots.any {
       locationView.isCheckedFor(it)
    }
