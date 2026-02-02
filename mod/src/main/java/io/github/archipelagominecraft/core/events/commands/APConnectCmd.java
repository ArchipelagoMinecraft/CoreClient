package io.github.archipelagominecraft.core.events.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCoreKt;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class APConnectCmd {
    public static void connectCommand(CommandDispatcher<CommandSourceStack> command) {


        // Connection Command
        command.register(Commands.literal("connect")
            .then(Commands.argument("server", StringArgumentType.string())
            .then(Commands.argument("port", IntegerArgumentType.integer())
            .then(Commands.argument("slot", StringArgumentType.string())
                .executes(context -> {
                    assert Minecraft.getInstance().player != null;
                    return ArchipelagoMinecraftClientCoreKt.getArchipelagoMinecraftCoreRegistration().getSlotRepresentingPlayer(Minecraft.getInstance().player).tryConnection(
                            StringArgumentType.getString(context, "server"),
                            IntegerArgumentType.getInteger(context, "port"),
                            StringArgumentType.getString(context, "slot"),
                            ""
                    );
                })
                .then(Commands.argument("password", StringArgumentType.string())
                    .executes(context -> {
                        assert Minecraft.getInstance().player != null;
                        return ArchipelagoMinecraftClientCoreKt.getArchipelagoMinecraftCoreRegistration().getSlotRepresentingPlayer(Minecraft.getInstance().player).tryConnection(
                                StringArgumentType.getString(context, "server"),
                                IntegerArgumentType.getInteger(context, "port"),
                                StringArgumentType.getString(context, "slot"),
                                StringArgumentType.getString(context, "password")
                        );
                    })
        )))));
    }
}