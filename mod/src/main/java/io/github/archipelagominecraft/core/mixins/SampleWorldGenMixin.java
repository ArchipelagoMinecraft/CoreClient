package io.github.archipelagominecraft.core.mixins;

//
//import io.github.archipelagominecraft.core.LogManager;
//import io.github.archipelagominecraft.core.Logger;
//import io.github.archipelagominecraft.core.SampleArchipelagoFeatureWorldSeedRando;
//import net.minecraft.client.Minecraft;
//import net.minecraft.server.WorldStem;
//import net.minecraft.world.level.storage.PrimaryLevelData;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyVariable;
//
//import java.util.OptionalLong;
//
//@Mixin(Minecraft.class)
//public abstract class SampleWorldGenMixin {
//
//    @Unique
//    private static final Logger archipelagoMinecraftClientCore$LOGGER = LogManager.getLogger(SampleWorldGenMixin.class);
//
//    @ModifyVariable(method = "doWorldLoad(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/WorldStem;Z)V",
//            at = @At("HEAD"), argsOnly = true)
//    private WorldStem overrideChunkSeed(WorldStem worldStem) {
//        Long forcedSeed = SampleArchipelagoFeatureWorldSeedRando.INSTANCE.getForcedSeed();
//        if (forcedSeed != null) {
//            var newWorldGen = worldStem.worldData().worldGenOptions().withSeed(OptionalLong.of(forcedSeed));
//            PrimaryLevelData worldData = (PrimaryLevelData) worldStem.worldData();
//            worldData.worldOptions = newWorldGen;
//            return new WorldStem(
//                    worldStem.resourceManager(),
//                    worldStem.dataPackResources(),
//                    worldStem.registries(),
//                    worldData
//            );
//        } else {
//            archipelagoMinecraftClientCore$LOGGER.info("No forced seed found");
//            return worldStem;
//        }
//    }
//}
//

