//? if <=1.12.2 {
/*package io.github.archipelagominecraft.core.mixins;


import io.github.archipelagominecraft.core.SampleArchipelagoFeatureWorldSeedRando;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldInfo.class)
public abstract class ForceSeedMixinLegacy {


    @Shadow
    public long randomSeed;

    @Inject(
            method = "getSeed",
            at = @At("HEAD")
    )
    private void overrideSeedForNewWorld(CallbackInfoReturnable<Long> cir) {
        Long forcedSeed = SampleArchipelagoFeatureWorldSeedRando.INSTANCE.getForcedSeed();
        if (forcedSeed != null) {
            this.randomSeed = forcedSeed;
        }
    }
}
*///? }
