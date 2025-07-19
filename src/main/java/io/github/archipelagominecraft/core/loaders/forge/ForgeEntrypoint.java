//? if forge {
/*package io.github.archipelagominecraft.core.loaders.forge;

import io.github.archipelagominecraft.core.ArchipelagoClientConstants;
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ArchipelagoClientConstants.MOD_ID)
public class ForgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ForgeEntrypoint() {
        LOGGER.info("Hello from ForgeEntrypoint!");
        ArchipelagoMinecraftClientCore.initialize();
    }
}
*///?}
