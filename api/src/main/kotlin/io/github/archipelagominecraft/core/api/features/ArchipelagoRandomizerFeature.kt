package io.github.archipelagominecraft.api.features

import io.github.archipelagominecraft.api.ArchipelagoIdentifiable
import com.mojang.serialization.Codec
import io.github.archipelagominecraft.api.compat.Player

interface ArchipelagoRandomizerFeature<D> : ArchipelagoIdentifiable {
    val dataCodec: Codec<D>

    fun receiveData(data: D)

    fun beforeWorldLoad() {}

    fun afterWorldLoad() {}

    fun onPlayerJoin(player: Player) {}

    fun onPlayerLeft(player: Player)  {}

}
