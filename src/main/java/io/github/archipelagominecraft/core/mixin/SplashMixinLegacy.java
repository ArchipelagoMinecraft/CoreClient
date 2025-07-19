//? if legacy {
/*package io.github.archipelagominecraft.core.mixin;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiMainMenu.class)
public class SplashMixinLegacy {

    @Shadow
    private String splashText;

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        String baseString = "[MIXIN] Hello from %LOADER% on Minecraft %MINECRAFT%";

        baseString = baseString.replace("%LOADER%", "Legacy Forge");

        //? if 1.21.4 {
        baseString = baseString.replace("%MINECRAFT%", "1.21.4");
        //?} else if 1.20.1 {
        /^baseString = baseString.replace("%MINECRAFT", "1.20.1");
        ^///?} else if 1.12.2 {
        /^baseString = baseString.replace("%MINECRAFT%", "1.12.2");
        ^///?}

        splashText = baseString;
    }
}
*///?}
