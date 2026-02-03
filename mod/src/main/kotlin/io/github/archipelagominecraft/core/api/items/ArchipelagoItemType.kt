package io.github.archipelagominecraft.core.api.items

import io.github.archipelagominecraft.core.api.ArchipelagoType


//todo utility methods to store rewards like minecraft items given to the player, until they log-in
// and manage multiple players
/**
 * Represents an Archipelago item type.
 *
 *
 * An Archipelago item type can have a `dataCodec` which is used
 * to serialize and deserialize the data associated with the item.
 *
 * Upon server startup, the `prepareItem` method will be called, the object given as a parameter can be used to check
 * if any archipelago slot the server is responsible for has received the item
 *
 * The `onItemReceived` method will be called when a slot the server is responsible for receives the item,
 * **even if no players are online**
 *
 * @param D The type of the data associated with the item.
 */
interface ArchipelagoItemType<D> : ArchipelagoType<D> {

    /**
     * Called upon server startup, allows the item type handler to query information about the item.
     * @param archipelagoItemView An ArchipelagoItemView object, that allows checking
     */
    fun prepareItem(archipelagoItemView: ArchipelagoItemView, data: D)



}
