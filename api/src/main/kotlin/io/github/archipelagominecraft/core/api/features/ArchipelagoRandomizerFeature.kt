package io.github.archipelagominecraft.core.api.features

import io.github.archipelagominecraft.core.api.ArchipelagoIdentifiable
import com.mojang.serialization.Codec
import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagominecraft.core.api.ArchipelagoType
import io.github.archipelagominecraft.core.api.compat.Player
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemView

/**
 * Represents a randomizer feature, with unique data per-multiworld, for example specifying the world seed or
 * structure randomization
 * The data it receives is from the archipelago slot data
 */
interface ArchipelagoRandomizerFeature<D> : ArchipelagoType<D> {


    /**
     * Called upon server startup, allows the feature handler to receive a view
     * @param archipelagoRandomizerFeatureView an object that allows querying the slot data for this feature for known slots
     */
    fun prepareFeature(archipelagoRandomizerFeatureView: ArchipelagoRandomizerFeatureView<D>)


}
