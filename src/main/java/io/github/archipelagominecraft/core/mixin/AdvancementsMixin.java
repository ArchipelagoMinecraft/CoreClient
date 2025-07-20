//? if fabric {
/*package io.github.archipelagominecraft.core.mixin;

import io.github.archipelagominecraft.core.loaders.fabric.AdvancementEarnEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//pretend this is in the vanilla mod
@Mixin(PlayerAdvancements.class)
public class AdvancementsMixin {

    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/Advancement;rewards()Lnet/minecraft/advancements/AdvancementRewards;"))
    public void onAchievementGranted(AdvancementHolder advancementHolder, String string, CallbackInfoReturnable<Boolean> cir) {
        AdvancementEarnEvent.EVENT.invoker().onAdvancementEarned(advancementHolder,player);
    }
}

*///?}
