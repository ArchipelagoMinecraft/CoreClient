//? if <=1.12.2 {
/*package io.github.archipelagominecraft.core.loaders.legacy;

import com.mojang.serialization.Codec;
import io.github.archipelagominecraft.core.ArchipelagoClientConstants;
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import io.github.archipelagominecraft.core.compat.Logger;
import io.github.archipelagominecraft.core.forgeLike.ForgeLikeEntrypointKt;
import io.github.archipelagominecraft.core.vanilla.ArchipelagoMinecraftVanilla;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

@Mod(modid = ArchipelagoClientConstants.MOD_ID)
public class LegacyForgeEntrypoint {

    public static final Logger LOGGER =
            ArchipelagoMinecraftClientCore.LOGGER;
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("Hello, world from 1.12.2!");
        LOGGER.info("using lib: "+  Codec.STRING);
        LOGGER.info("Using minecraft class "+ Minecraft.getMinecraft().gameDir);
        ArchipelagoMinecraftClientCore.initialize();
    }

    @Mod.EventHandler
    public void init(FMLServerAboutToStartEvent preinit) {
        LOGGER.info("Legacy Forge Entrypoint initialized.");
        ForgeLikeEntrypointKt.serverAboutToStart();
    }

}

*///?}
