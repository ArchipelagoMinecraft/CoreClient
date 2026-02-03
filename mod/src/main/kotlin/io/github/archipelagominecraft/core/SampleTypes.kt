package io.github.archipelagominecraft.core

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.archipelagominecraft.core.api.compat.Player
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeature
import io.github.archipelagominecraft.core.api.features.ArchipelagoRandomizerFeatureView
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemView
import io.github.archipelagominecraft.core.api.items.hasAnySlotReceivedItem
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationType
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationView
import io.github.archipelagominecraft.core.api.locations.isCheckedForAnySlot


private val LOGGER: Logger = LogManager.getLogger("ArchipelagoSampleTypes")

private const val SAMPLE_NAMESPACE = "apsample"

object SampleArchipelagoItemType : ArchipelagoItemType<SampleArchipelagoItemType.SampleData> {
    override val namespace: String = "apsample"
    override val typeId: String = "sample_type"
    override val dataCodec: Codec<SampleData> = RecordCodecBuilder.create { builder ->
        builder.group(
            Codec.STRING.fieldOf("value").forGetter { it.value }
        ).apply(builder, ::SampleData)
    }

    override fun prepareItem(archipelagoItemView: ArchipelagoItemView, data: SampleData) {
        LOGGER.info("Sample item preparation, was unlocked by someone: ${ArchipelagoMinecraftCoreRegistration.hasAnySlotReceivedItem(archipelagoItemView)}")
        val map = ArchipelagoMinecraftCoreRegistration.serverManagedSlots.map {
            it to archipelagoItemView.hasSlotReceivedItem(it)
        }
        LOGGER.info("For each slot, was it unlocked ? $map")

        archipelagoItemView.registerOnItemReceivedListener {

            LOGGER.info("Item received to Slot: $it, $data")
        }
    }



    data class SampleData(val value: String)




}

object SampleArchipelagoFeatureWorldSeedRando: ArchipelagoRandomizerFeature<Long>{
    override val namespace: String = "apsample"
    override val typeId: String = "world_seed_rando"
    override val dataCodec: Codec<Long> = Codec.LONG

    private var _forcedSeed: Long? = null
    override fun prepareFeature(archipelagoRandomizerFeatureView: ArchipelagoRandomizerFeatureView<Long>) {
        // feature specific to the whole server, so we don't care about slot ids, and we ensure only a single slot is
        // present with .single()
        val data = archipelagoRandomizerFeatureView.slotDatas.values.single()
        LOGGER.info("Sample world seed rando feature, could force seed to $data")
        this._forcedSeed = data
    }

    // See SampleWorldGenMixin
    fun getForcedSeed(): Long? = _forcedSeed

}

object SampleArchipelagoLocationType: ArchipelagoLocationType<SampleArchipelagoLocationType.Data>{

    data class Data(val locvalue: String)
    override val dataCodec: Codec<Data> = RecordCodecBuilder.create { builder ->
        builder.group(
            Codec.STRING.fieldOf("locvalue").forGetter { it.locvalue }
        ).apply(builder, ::Data)
    }


    override val namespace: String = SAMPLE_NAMESPACE

    override val typeId: String = "location"
    private lateinit var locView: ArchipelagoLocationView

    override fun prepareLocation(
        locationView: ArchipelagoLocationView,
        itemData: Data,
    ) {
        LOGGER.info("Sample location preparation, was unlocked by someone: ${ArchipelagoMinecraftCoreRegistration.isCheckedForAnySlot(locationView)}")
        val map = ArchipelagoMinecraftCoreRegistration.serverManagedSlots.map {
            it to locationView.isCheckedFor(it)
        }
        LOGGER.info("For each slot, was it checked ? $map")
        this.locView = locationView
    }


    //pretend this is some event handler
    fun onPlayerDidSomething(player: Player){
        val slot = ArchipelagoMinecraftCoreRegistration.getSlotRepresentingPlayer(player)
        locView.checkFor(listOf(slot))
    }


}
