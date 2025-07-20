//? if forgeLike {
package io.github.archipelagominecraft.core.compat.forgeLike


typealias ForgeLike =
//? if neoforge {
        net.neoforged.neoforge.common.NeoForge
//?} else if forge {
        /*net.minecraftforge.common.MinecraftForge
*///?}

typealias Event =
//? if neoforge {
        net.neoforged.bus.api.Event
//?} else if forge {
        /*net.minecraftforge.fml.common.eventhandler.Event
*///?}

typealias EventBus =
//? if neoforge {
        net.neoforged.bus.api.IEventBus
//?} else if forge {
        /*net.minecraftforge.fml.common.eventhandler.EventBus
*///?}

typealias SubscribeEvent =
//? if neoforge {
        net.neoforged.bus.api.SubscribeEvent
//?} else if forge {
        /*net.minecraftforge.fml.common.eventhandler.SubscribeEvent
*///?}


