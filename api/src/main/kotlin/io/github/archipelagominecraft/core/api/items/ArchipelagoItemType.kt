package io.github.archipelagominecraft.api.items

import io.github.archipelagominecraft.api.ArchipelagoType
import io.github.archipelagominecraft.api.compat.Player


/**
 * Represents an item type.
 *
 * Upon player connection, all registered item types will have their `prepareItem` method called to prepare the item data.
 * If the item has been received previously by any players, the `hasReceivedPreviously` parameter will contain a list of those players.
 *
 * This can be used to do things like make sure crafting recipes that were previously received are unlocked, and those
 * not yet received are locked.
 * (taking as an example a crafting recipe item type)
 *
 * @param D The type of the data associated with the item.
 */
interface ArchipelagoItemType<D>: ArchipelagoType<D> {
    /**
     * Called upon player login to prepare the item with the given data.
     *
     * @param hasReceivedPreviously A list of players who have received this item previously.
     * @param itemData The data associated with the item.
     */
    fun prepareItemForPlayer(player: Player, hasReceivedPreviously: Boolean, itemData: D)


    /**
     * Called upon world load to prepare the item with the given data.
     *
     * @param hasReceivedPreviously Indicates if the item has been received previously by any players.
     * @param itemData The data associated with the item.
     */
    fun prepareItemForWorld(hasReceivedPreviously: Boolean, itemData: D)

    /**
     * Called when the item is received by a player.
     *
     * @param player The player who received the item.
     * @param itemData The data associated with the item.
     */
    fun onItemReceived(player: Player,itemData: D)

}
