package io.github.archipelagominecraft.core.api.features

import io.github.archipelagominecraft.core.api.ArchipelagoSlot


/**
 * Represents a view of an Archipelago item
 */
@Suppress("unused")
interface ArchipelagoRandomizerFeatureView<D> {

    /**
     * Contains the data corresponding to each slot
     */
    val slotDatas: Map<ArchipelagoSlot,D>

}
