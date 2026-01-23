package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeatureView
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationView


class ArchipelagoRandomizationFeatureImpl<D>(
    override val slotDatas: Map<ArchipelagoSlot, D>
) : ArchipelagoRandomizerFeatureView<D>
