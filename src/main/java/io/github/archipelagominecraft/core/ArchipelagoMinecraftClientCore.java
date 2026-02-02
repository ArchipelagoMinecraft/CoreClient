//? if modern {
package io.github.archipelagominecraft.core;

import com.mojang.logging.LogUtils;
import io.github.archipelagominecraft.core.events.commands.APConnectCmd;
//? if fabric {
/*import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
*///?} elif neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
//?}
import org.slf4j.Logger;

public class ArchipelagoMinecraftClientCore {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize() {
        LOGGER.info("Hello from ArchipelagoMinecraftClientCore!");

        //? if fabric {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            APConnectCmd.connectCommand(dispatcher);
        });
        //?}

    }

    //? if neoforge {
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        APConnectCmd.connectCommand(event.getDispatcher());
    }
    //?}
}
//?}
