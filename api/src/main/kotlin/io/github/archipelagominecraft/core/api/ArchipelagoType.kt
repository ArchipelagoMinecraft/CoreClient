package io.github.archipelagominecraft.api

import com.mojang.serialization.Codec

interface ArchipelagoType<D> : ArchipelagoIdentifiable {
    val dataCodec : Codec<D>
}
