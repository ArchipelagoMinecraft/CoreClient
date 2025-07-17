//? if forge {
/*package io.github.archipelagominecraft.core.loaders.forge;

import io.github.archipelagominecraft.core.ExampleMod;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod("archipelago_client_core") //todo id from constants
public class ForgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ForgeEntrypoint() {
        LOGGER.info("Hello from ForgeEntrypoint!");
        ExampleMod.initialize();
    }
}
*///?}
