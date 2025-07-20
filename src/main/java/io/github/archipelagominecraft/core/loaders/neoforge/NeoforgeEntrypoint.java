//? if neoforge {
package io.github.archipelagominecraft.core.loaders.neoforge;

import io.github.archipelagominecraft.core.ArchipelagoClientConstants;
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import com.mojang.logging.LogUtils;
import io.github.archipelagominecraft.core.api.ArchipelagoLocationType;
import io.github.archipelagominecraft.core.compat.forgeLike.ForgeLikeKt;
import io.github.archipelagominecraft.core.forgeLike.ForgeLikeEntrypointKt;
import io.github.archipelagominecraft.core.forgeLike.LocationTypeRegistrationEvent;
import io.github.archipelagominecraft.core.vanilla.AdvancementLocationType;
import io.github.archipelagominecraft.core.vanilla.ArchipelagoMinecraftVanilla;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;

@Mod(ArchipelagoClientConstants.MOD_ID)
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint(IEventBus modBus) {
        LOGGER.info("Hello from NeoforgeEntrypoint!");
        ArchipelagoMinecraftClientCore.initialize();
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent e) -> ForgeLikeEntrypointKt.serverAboutToStart());
    }

}
//?}
