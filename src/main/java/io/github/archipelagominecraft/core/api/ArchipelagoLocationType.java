package io.github.archipelagominecraft.core.api;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface ArchipelagoLocationType<S extends ArchipelagoLocationType<S, T>,T> {
    ResourceLocation getId();

    void registerLocation(ArchipelagoLocationView<S> archipelagoID, T itemData);

    Codec<T> getDataCodec();
}
