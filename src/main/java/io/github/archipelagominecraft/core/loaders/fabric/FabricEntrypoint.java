//? if fabric {
/*package io.github.archipelagominecraft.core.loaders.fabric;

import com.mojang.logging.LogUtils;
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import io.github.archipelagominecraft.core.vanilla.AdvancementLocationType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;

public class FabricEntrypoint implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Event<ArchipelagoMinecraftClientCore.RegisterLocationTypeCallback> REGISTER_LOCATION_TYPE =
            EventFactory.createArrayBacked(
                    ArchipelagoMinecraftClientCore.RegisterLocationTypeCallback.class,
                    (listeners) -> (manager) -> {
                        for (ArchipelagoMinecraftClientCore.RegisterLocationTypeCallback listener : listeners) {
                            listener.onRegister(manager);
                        }
                    });

    @Override
    public void onInitialize() {
        LOGGER.info("Hello from FabricEntrypoint!");
        ArchipelagoMinecraftClientCore.initialize();
        vanillaMod();

        ServerLifecycleEvents.SERVER_STARTING.register(s -> {
            REGISTER_LOCATION_TYPE.invoker().onRegister(ArchipelagoMinecraftClientCore::registerLocationType);
            ArchipelagoMinecraftClientCore.afterRegistration();
        });
    }

    private void vanillaMod() {
        REGISTER_LOCATION_TYPE.register(m ->
                m.registerLocationType(new AdvancementLocationType())
        );
    }
}
*///?}
