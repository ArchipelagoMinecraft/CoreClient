//? if modern {
package io.github.archipelagominecraft.core.mixin;

import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public class SplashMixinModern {

    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    public void getSplash(CallbackInfoReturnable<SplashRenderer> cir) {
        String baseString = "[MIXIN] Hello from %LOADER% on Minecraft %MINECRAFT%";

        /// https://stonecutter.kikugie.dev/stonecutter/guide/comments

        //? if fabric {
        /*baseString = baseString.replace("%LOADER%", "Fabric Loader");
        *///?} else if neoforge {
        baseString = baseString.replace("%LOADER%", "NeoForge");
         //?} else {
        /*baseString = baseString.replace("%LOADER%", "Legacy Forge");
         *///?}

        //? if 1.21.4 {
        baseString = baseString.replace("%MINECRAFT%", "1.21.4");
        //?} else if 1.20.1 {
        /*baseString = baseString.replace("%MINECRAFT", "1.20.1");*/
        //?}


        ArchipelagoMinecraftClientCore.LOGGER.info(baseString);
        cir.setReturnValue(new SplashRenderer(baseString));
    }
}
//?}
