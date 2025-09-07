package io.github.archipelagominecraft.api.locations

import io.github.archipelagominecraft.api.ArchipelagoType


/**
 * Represents a location type in Archipelago
 *
 * A location type needs an `id` in the format `<namespace>:<typeId>`
 *
 * A location can have a `dataCodec` which is used
 * to serialize and deserialize the data associated with the location.
 *
 * Upon world load, all registered location types will have their `prepareLocation` method called for
 * each location of that type.
 *
 * This method is used to prepare the location data, for example, lock or unlock advancements, depending on
 * the location view state.
 * (If the player already checked the location, the corresponding advancement should be unlocked)
 *
 * @param D The type of the data associated with the location
 */
interface ArchipelagoLocationType<D>: ArchipelagoType<D> {
    fun prepareLocation(locationView: ArchipelagoLocationView, itemData: D)
}
