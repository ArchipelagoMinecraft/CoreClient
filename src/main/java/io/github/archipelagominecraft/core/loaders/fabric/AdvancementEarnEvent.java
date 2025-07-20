//? if fabric {
/*package io.github.archipelagominecraft.core.loaders.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

public interface AdvancementEarnEvent {
    Event<AdvancementEarnEvent> EVENT = EventFactory.createArrayBacked(AdvancementEarnEvent.class,
        (listeners) -> (advancement, player) -> {
            for (AdvancementEarnEvent listener : listeners) {
                listener.onAdvancementEarned(advancement, player);
            }
        });

    void onAdvancementEarned(AdvancementHolder advancementHolder, ServerPlayer player);
}
*///?}
