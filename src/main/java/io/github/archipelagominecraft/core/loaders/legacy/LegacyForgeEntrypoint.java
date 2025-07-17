//? if legacy {
package io.github.archipelagominecraft.core.loaders.legacy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = LegacyForgeEntrypoint.MODID
)
public class LegacyForgeEntrypoint {
    public static final String MODID = "archipelago_minecraft_core";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("Hello, world from 1.12.2!");
    }
}

//?}
