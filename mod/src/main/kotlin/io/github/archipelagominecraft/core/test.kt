package io.github.archipelagominecraft.core

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.archipelagominecraft.api.compat.Player
import io.github.archipelagominecraft.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.api.items.ArchipelagoItemType
import kotlin.properties.Delegates

class SampleType : ArchipelagoItemType<SampleType.SampleData> {
    override fun prepareItemForPlayer(
        player: Player,
        hasReceivedPreviously: Boolean,
        itemData: SampleData
    ) {
        TODO("Not yet implemented")
    }

    override fun prepareItemForWorld(
        hasReceivedPreviously: Boolean,
        itemData: SampleData
    ) {
        TODO("Not yet implemented")
    }

    override fun onItemReceived(
        player: Player,
        itemData: SampleData
    ) {
        TODO("Not yet implemented")
    }

    override val namespace: String = "apsample"
    override val typeId: String = "sample_type"
    override val dataCodec: Codec<SampleData> = RecordCodecBuilder.create { builder ->
        builder.group(
            Codec.STRING.fieldOf("value").forGetter { it.value }
        ).apply(builder, ::SampleData)
    }

    data class SampleData(val value: String)

}

class WorldSeedRando: ArchipelagoRandomizerFeature<Long>{
    override val namespace: String = "apsample"
    override val typeId: String = "world_seed_rando"
    override val dataCodec: Codec<Long> = Codec.LONG

    var forcedSeed: Long by Delegates.notNull()

    override fun receiveData(data: Long) {
        forcedSeed = data
    }

    override fun beforeWorldLoad() {
        //todo use mixin to set the world seed
    }
}
