//? if legacy {
/*package io.github.archipelagominecraft.core.loaders.legacy;

import com.mojang.serialization.Codec;
import io.github.archipelagominecraft.core.ArchipelagoClientConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ArchipelagoClientConstants.MOD_ID)
public class LegacyForgeEntrypoint {

    public static final Logger LOGGER = LogManager.getLogger(ArchipelagoClientConstants.MOD_NAME);

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("Hello, world from 1.12.2!");
        LOGGER.info("using lib: {}", Codec.STRING);
    }
}

*///?}
