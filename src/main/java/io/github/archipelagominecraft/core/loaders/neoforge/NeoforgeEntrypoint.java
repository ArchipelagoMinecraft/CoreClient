//? if neoforge {
package io.github.archipelagominecraft.core.loaders.neoforge;

import io.github.archipelagominecraft.core.ArchipelagoClientConstants;
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import com.mojang.logging.LogUtils;
import io.github.archipelagominecraft.core.ModConstants;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ArchipelagoClientConstants.MOD_ID)
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint() {
        LOGGER.info("Hello from NeoforgeEntrypoint!");
        ArchipelagoMinecraftClientCore.initialize();
    }
}
//?}
