package io.github.archipelagominecraft.core.vanilla;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.archipelagominecraft.core.api.ArchipelagoLocationType;
import io.github.archipelagominecraft.core.api.ArchipelagoLocationView;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Set;

@EventBusSubscriber()
public class AdvancementLocationType implements
        ArchipelagoLocationType<AdvancementLocationType, AdvancementLocationType.AdvancementLocationTypeData> {

    private static final Codec<AdvancementLocationTypeData> DATA_CODEC = RecordCodecBuilder
            .create(b ->
                    b.group(
                            ResourceKey.codec(Registries.ADVANCEMENT)
                                    .fieldOf("advancement_id")
                                    .forGetter(a -> a.advancementId)
                    ).apply(b, AdvancementLocationTypeData::new)
            );

    @Override
    public Codec<AdvancementLocationTypeData> getDataCodec() {
        return DATA_CODEC;
    }

    public static class AdvancementLocationTypeData {
        public final ResourceKey<Advancement> advancementId;

        public AdvancementLocationTypeData(ResourceKey<Advancement> advancementId) {
            this.advancementId = advancementId;
        }
    }

    public static final ResourceLocation ID = ResourceLocation.
            fromNamespaceAndPath("apvanilla", "advancement");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void registerLocation(ArchipelagoLocationView<AdvancementLocationType> archipelagoID, AdvancementLocationTypeData itemData) {
        advancementsLocationMap.put(itemData.advancementId.location(), archipelagoID);
    }

    private final HashMultimap<ResourceLocation, ArchipelagoLocationView<AdvancementLocationType>> advancementsLocationMap = HashMultimap.create();

    @SubscribeEvent()
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            for (AdvancementHolder advancement : player.server.getAdvancements().getAllAdvancements()) {
                Set<ArchipelagoLocationView<AdvancementLocationType>> locations = advancementsLocationMap.get(advancement.id());
                if (locations.stream().anyMatch(ArchipelagoLocationView::isChecked)) {
                    for (String remainingCriterion : player.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) {
                        player.getAdvancements().award(advancement, remainingCriterion);
                    }
                }
                if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                    for (ArchipelagoLocationView<AdvancementLocationType> archipelagoLocationView : locations) {
                        archipelagoLocationView.check();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAdvancementAchieve(AdvancementEvent.AdvancementEarnEvent event) {
        Set<ArchipelagoLocationView<AdvancementLocationType>> archipelagoLocationViews = advancementsLocationMap.get(event.getAdvancement().id());
        for (ArchipelagoLocationView<AdvancementLocationType> archipelagoLocationView : archipelagoLocationViews) {
            archipelagoLocationView.check();
        }
    }
}
