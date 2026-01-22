//? if modern {
package io.github.archipelagominecraft.core;

import com.mojang.logging.LogUtils;
import io.github.archipelagominecraft.core.events.commands.APConnectCmd;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;

public class ArchipelagoMinecraftClientCore {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize() {
        LOGGER.info("Hello from ArchipelagoMinecraftClientCore!");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            APConnectCmd.connectCommand(dispatcher);
        });
    }
}
//?}
