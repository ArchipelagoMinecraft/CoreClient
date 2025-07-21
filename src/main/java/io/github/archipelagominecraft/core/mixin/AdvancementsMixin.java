package io.github.archipelagominecraft.core.mixin;

import io.github.archipelagominecraft.core.vanilla.events.AdvancementEarnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if <=1.12.2 {
/*import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
*///?} else {
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.PlayerAdvancements;
//?}

//pretend this is in the vanilla mod
@Mixin(PlayerAdvancements.class)
public class AdvancementsMixin {

    @Shadow
    private /*? if <=1.12.2 {*/ /*EntityPlayerMP *//*?} else {*/ ServerPlayer /*?}*/ player;

    //? if <=1.12.2 {
    /*@Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/Advancement;getRewards()Lnet/minecraft/advancements/AdvancementRewards;"))
    public void onAchievementGranted(Advancement adv, String p_192750_2_, CallbackInfoReturnable<Boolean> cir) {
        AdvancementEarnEvent.EVENT.trigger(new AdvancementEarnEvent(
                adv,
                adv.getId(),
                player
        ));
    }
    *///?} else {
    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/Advancement;rewards()Lnet/minecraft/advancements/AdvancementRewards;"))
    public void onAchievementGranted(AdvancementHolder advancementHolder, String string, CallbackInfoReturnable<Boolean> cir) {
        AdvancementEarnEvent.EVENT.trigger(new AdvancementEarnEvent(
                advancementHolder.value(),
                advancementHolder.id()
                , player));
    }
    //?}

}
