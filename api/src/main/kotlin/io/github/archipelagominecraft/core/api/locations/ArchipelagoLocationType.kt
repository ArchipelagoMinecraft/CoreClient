package io.github.archipelagominecraft.core.api.locations

import io.github.archipelagominecraft.core.api.ArchipelagoType


/**
 * Represents a location type in Archipelago
 *
 * A location type needs an `id` in the format `<namespace>:<typeId>`
 *
 * A location can have a `dataCodec` which is used
 * to serialize and deserialize the data associated with the location.
 *
 *
 * @param D The type of the data associated with the location
 */
interface ArchipelagoLocationType<D>: ArchipelagoType<D> {
    /**
     * Called on server startup, allows to prepare the location's status in game and provides an object to mark
     * the location as checked, and to query its status
     */
    fun prepareLocation(locationView: ArchipelagoLocationView, itemData: D)
}
