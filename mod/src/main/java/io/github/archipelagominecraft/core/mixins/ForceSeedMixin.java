//? if >1.12.2 {
package io.github.archipelagominecraft.core.mixins;


import io.github.archipelagominecraft.core.SampleArchipelagoFeatureWorldSeedRando;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldOptions.class)
public abstract class ForceSeedMixin {


    @Shadow
    public long seed;

    @Inject(method = "seed", at = @At("HEAD"), cancellable = true)
    private void overrideChunkSeed(CallbackInfoReturnable<Long> cir) {
//        Long forcedSeed = SampleArchipelagoFeatureWorldSeedRando.INSTANCE.getForcedSeed();
//        if (forcedSeed != null) {
//            // access transformer test as well
//            this.seed = forcedSeed;
//        }
    }
}
//? }

