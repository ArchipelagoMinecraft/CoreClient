package io.github.archipelagominecraft.core.api

import com.mojang.serialization.Codec


interface ArchipelagoLocationType<S : ArchipelagoLocationType<S, T>, T> {
    val namespace: String
    val typeId: String
    val id: String
        get() = this.namespace + ":" + this.typeId

    fun registerLocation(locationView: ArchipelagoLocationView<S>, itemData: T)

    val dataCodec: Codec<T>
}
