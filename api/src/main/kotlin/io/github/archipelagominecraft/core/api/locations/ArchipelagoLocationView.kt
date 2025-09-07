package io.github.archipelagominecraft.api.locations

import io.github.archipelagominecraft.api.compat.Player


/**
 * Represents a view of an Archipelago location that can be checked for a specific player.
 *
 * This interface is used
 * to determine if a player has checked a specific location and to mark the location as checked
 * for that player.
 *
 * For locations that don't depend on a specific player, you should check them for all players
 */
@Suppress("unused")
interface ArchipelagoLocationView {
    fun checkFor(player: Player)
    fun isCheckedFor(player: Player): Boolean
}
