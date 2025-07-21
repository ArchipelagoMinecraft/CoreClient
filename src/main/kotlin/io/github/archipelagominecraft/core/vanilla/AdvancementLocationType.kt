package io.github.archipelagominecraft.core.vanilla

import com.google.common.collect.HashMultimap
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.archipelagominecraft.core.api.ArchipelagoLocationView
import io.github.archipelagominecraft.core.vanilla.compat.ServerPlayer
import io.github.archipelagominecraft.core.vanilla.AdvancementLocationType.AdvancementLocationTypeData
import io.github.archipelagominecraft.core.vanilla.compat.ResourceLocation
import io.github.archipelagominecraft.core.vanilla.events.AdvancementEarnEvent
import io.github.archipelagominecraft.core.vanilla.events.PlayerLoginEvent
import net.minecraft.server.MinecraftServer

//? if forgeLike {

import io.github.archipelagominecraft.core.vanilla.compat.CompatCodecs

//?}

//? if >1.12.2 {
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
//? if fabric {
/*import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents

*///?}
//?}



private val DATA_CODEC: Codec<AdvancementLocationTypeData> = RecordCodecBuilder.create { builder ->
    builder.group(
        //? if >1.12.2 {
        ResourceKey.codec(Registries.ADVANCEMENT).xmap(
            {it.location()}) { ResourceKey.create(Registries.ADVANCEMENT, it) }
            //?} else {
        /*CompatCodecs.RESOURCE_LOCATION
            *///?}
            .fieldOf("advancement_id").forGetter { it.advancementId })
        .apply(builder, ::AdvancementLocationTypeData)
}
// pretend this is in the vanilla mod
class AdvancementLocationType : ArchipelagoVanillaLocation<AdvancementLocationType, AdvancementLocationTypeData>() {
    override val namespace: String = "apvanilla"
    override val typeId: String = "advancement"
    override val dataCodec = DATA_CODEC

    data class AdvancementLocationTypeData(val advancementId: ResourceLocation)


    override fun registerLocation(
        locationView: ArchipelagoLocationView<AdvancementLocationType>, itemData: AdvancementLocationTypeData
    ) {
        advancementsLocationMap.put(itemData.advancementId, locationView)
    }

    private val advancementsLocationMap: HashMultimap<ResourceLocation, ArchipelagoLocationView<AdvancementLocationType>> =
        HashMultimap.create<ResourceLocation, ArchipelagoLocationView<AdvancementLocationType>>()


    //? if <=1.12.2 {
    /*typealias Advancement = net.minecraft.advancements.Advancement
    typealias AdvancementProgress = net.minecraft.advancements.AdvancementProgress
    typealias PlayerAdvancements = net.minecraft.advancements.PlayerAdvancements

    data class AdvancementHolder(val value: Advancement, val id: ResourceLocation) {
        fun id() = id
        fun value() = value
    }

    fun PlayerAdvancements.getOrStartProgress(adv: AdvancementHolder): AdvancementProgress =
        this.getProgress(adv.value)

    fun PlayerAdvancements.award(adv: AdvancementHolder, criteria: String) =
        this.grantCriterion(adv.value, criteria)

    val AdvancementProgress.remainingCriteria: Iterable<String>
        get() = this.remaningCriteria
    *///?}

    val MinecraftServer.allAdvancementsCompat: Collection<AdvancementHolder>
        get() =
        //? if >1.12.2 {
            this.advancements.allAdvancements
        //?} else {
    /*this.advancementManager.advancements.map { AdvancementHolder(it, it.id) }
    *///?}

    inline val ServerPlayer.serverCompat: MinecraftServer
        get() =
            //? if <= 1.12.2 {
        /*this.server
        *///?} else if >=1.21.4 && <= 1.21.5 {
            this.serverLevel().server
    //?} else {
            /*this.level().server
    *///?}

    fun onPlayerJoin(playerLoginEvent: PlayerLoginEvent) {
        val player = playerLoginEvent.player as? ServerPlayer ?: return
        player.serverCompat.allAdvancementsCompat.forEach { advancement ->
            val locations = advancementsLocationMap.get(advancement.id())
            if (locations.any { it.isCheckedFor(player) }) {
                player.advancements.getOrStartProgress(advancement).remainingCriteria
                    .forEach { player.advancements.award(advancement, it) }
            }
            if (player.advancements.getOrStartProgress(advancement).isDone) {
                locations.forEach { it.checkFor(player) }
            }
        }
    }

    fun onAdvancementAchieve(event: AdvancementEarnEvent) {
        println("onAdvancementAchieve: $event")
        advancementsLocationMap.get(event.id).forEach { it.checkFor(event.player) }
    }

    override fun registerListeners() {
        PlayerLoginEvent.EVENT.registerListener(::onPlayerJoin)
        AdvancementEarnEvent.EVENT.registerListener(::onAdvancementAchieve)
    }
}
