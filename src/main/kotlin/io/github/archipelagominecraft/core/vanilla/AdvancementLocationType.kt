package io.github.archipelagominecraft.core.vanilla

import com.google.common.collect.HashMultimap
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.archipelagominecraft.core.api.ArchipelagoLocationType
import io.github.archipelagominecraft.core.api.ArchipelagoLocationView
import io.github.archipelagominecraft.core.compat.CompatCodecs
import io.github.archipelagominecraft.core.compat.Player
import io.github.archipelagominecraft.core.compat.ServerPlayer
import io.github.archipelagominecraft.core.vanilla.AdvancementLocationType.AdvancementLocationTypeData
import io.github.archipelagominecraft.core.compat.ResourceLocation
import net.minecraft.server.MinecraftServer

//? if forgeLike {

import io.github.archipelagominecraft.core.compat.forgeLike.ForgeLike
import io.github.archipelagominecraft.core.compat.forgeLike.SubscribeEvent
//?}

//? if >1.12.2 {
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
//? if fabric {
/*import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import io.github.archipelagominecraft.core.loaders.fabric.AdvancementEarnEvent
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
class AdvancementLocationType : ArchipelagoLocationType<AdvancementLocationType, AdvancementLocationTypeData> {
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

    val MinecraftServer.allAdvancements: List<AdvancementHolder>
        get() = this.advancementManager.advancements.map { AdvancementHolder(it, it.id) }

    fun PlayerAdvancements.getOrStartProgress(adv: AdvancementHolder): AdvancementProgress =
        this.getProgress(adv.value)

    fun PlayerAdvancements.award(adv: AdvancementHolder, criteria: String) =
        this.grantCriterion(adv.value, criteria)

    val AdvancementProgress.remainingCriteria: Iterable<String>
        get() = this.remaningCriteria
    *///?} else {
    val MinecraftServer.allAdvancements: Collection<AdvancementHolder>
        get() = this.advancements.allAdvancements
    //?}
    //? if neoforge {
    val AdvancementEarnEvent.holder: AdvancementHolder
        get() = this.advancement
    //?}

    fun onPlayerJoin(player: ServerPlayer) {
        player.server.allAdvancements.forEach { advancement ->
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

    fun onAdvancementAchieve(advancement: AdvancementHolder, player: ServerPlayer) {
        advancementsLocationMap.get(advancement.id).forEach { it.checkFor(player) }
    }

    //? if forgeLike {
        typealias PlayerLoggedInEvent =
        //? if forge {
            /*net.minecraftforge.fml.common.gameevent.PlayerEvent
        *///?} else if neoforge {
                net.neoforged.neoforge.event.entity.player.PlayerEvent
        //?}

        typealias AdvancementEarnEvent =
        //? if forge {
                /*net.minecraftforge.event.entity.player.AdvancementEvent
        *///?} else if neoforge {
                net.neoforged.neoforge.event.entity.player.AdvancementEvent
        //?}

        //? if forge {
            /*val PlayerLoggedInEvent.entity: Player
                get() = this.player
            val AdvancementEarnEvent.holder
                get() = AdvancementHolder(this.advancement,this.advancement.id)
    *///?}
    //?}

    fun registerListeners() {
        //? if forgeLike {

        ForgeLike.EVENT_BUS.register(object {
            @SubscribeEvent
            fun onEvent(it: PlayerLoggedInEvent) {
                onPlayerJoin(it.entity as? ServerPlayer ?: return)
            }
        })

        ForgeLike.EVENT_BUS.register(object{
            @SubscribeEvent
            fun onEvent(it: AdvancementEarnEvent) {
                onAdvancementAchieve(it.holder, it.entity as? ServerPlayer ?: return)
            }
        })
        //?} else if fabric {
        /*ServerEntityEvents.ENTITY_LOAD.register { e, _ -> (e as? ServerPlayer)?.let { onPlayerJoin(it) } }
        AdvancementEarnEvent.EVENT.register(::onAdvancementAchieve)
        *///?}
    }
}
