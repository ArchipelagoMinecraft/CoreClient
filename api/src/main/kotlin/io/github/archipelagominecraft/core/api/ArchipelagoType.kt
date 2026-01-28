package io.github.archipelagominecraft.core.api

import com.mojang.serialization.Codec

interface ArchipelagoType<D> : ArchipelagoIdentifiable {
    val dataCodec : Codec<D>

}


