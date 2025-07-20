package io.github.archipelagominecraft.core.compat


typealias ServerPlayer =
//? if <=1.12.2 {
        /*net.minecraft.entity.player.EntityPlayerMP
*///?} else {
net.minecraft.server.level.ServerPlayer
//?}

typealias Player =
//? if <=1.12.2 {
        /*net.minecraft.entity.player.EntityPlayer
*///?} else {
net.minecraft.world.entity.player.Player
//?}

typealias ResourceLocation =
//? if <=1.12.2 {
        /*net.minecraft.util.ResourceLocation
*///?} else {
net.minecraft.resources.ResourceLocation
//?}
