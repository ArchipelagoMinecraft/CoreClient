package io.github.archipelagominecraft.core.vanilla.events

import io.github.archipelagominecraft.core.vanilla.compat.ResourceLocation
import io.github.archipelagominecraft.core.vanilla.compat.ServerPlayer
import net.minecraft.advancements.Advancement

data class AdvancementEarnEvent(
    val advancementHolder: Advancement,
    val id: ResourceLocation,
    val player: ServerPlayer
) {
    companion object {
        @JvmField
        val EVENT: ArchipelagoEvent<AdvancementEarnEvent> = ArchipelagoEvent()
    }
}
