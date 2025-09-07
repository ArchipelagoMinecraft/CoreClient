package io.github.archipelagominecraft.api

import io.github.archipelagominecraft.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.api.locations.ArchipelagoLocationType

object ArchipelagoMinecraftCoreRegistration {
    private val _locationTypes: MutableMap<String, ArchipelagoLocationType<*>> = mutableMapOf()
    val locationTypes: Map<String, ArchipelagoLocationType<*>> = _locationTypes

    private val _itemTypes: MutableMap<String, ArchipelagoItemType<*>> = mutableMapOf()
    val itemTypes: Map<String, ArchipelagoItemType<*>> = _itemTypes

    private val _randomizerFeatures: MutableMap<String, ArchipelagoRandomizerFeature<*>> = mutableMapOf()
    val randomizerFeatures: Map<String, ArchipelagoRandomizerFeature<*>> = _randomizerFeatures


    @Suppress("unused")
    @JvmStatic
    fun registerLocationType(locationType: ArchipelagoLocationType<*>) {
        check(!_locationTypes.containsKey(locationType.id)) { "Duplicated ArchipelagoLocationType: " + locationType.id }
        _locationTypes.put(locationType.id, locationType)
    }

    @Suppress("unused")
    @JvmStatic
    fun registerItemType(itemType: ArchipelagoItemType<*>) {
        check(!_itemTypes.containsKey(itemType.id)) { "Duplicated ArchipelagoItemType: " + itemType.id }
        _itemTypes.put(itemType.id, itemType)
    }

    @Suppress("unused")
    @JvmStatic
    fun registerRandomizerFeature(feature: ArchipelagoRandomizerFeature<*>) {
        check(!_randomizerFeatures.containsKey(feature.id)) { "Duplicated ArchipelagoRandomizerFeature: " + feature.id }
        _randomizerFeatures.put(feature.id, feature)
    }


}
