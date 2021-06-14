package com.alphastudios.cavebiomeapi.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeAccess.class)
public class BiomeAccessMixin {
    @Final
    @Shadow
    static int CHUNK_CENTER_OFFSET;

    @Redirect(method = "getBiomeForNoiseGen(Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/world/biome/Biome;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeAccess$Storage;getBiomeForNoiseGen(Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/world/biome/Biome;"))
    private Biome generateUndergroundFeatures(BiomeAccess.Storage storage, ChunkPos chunkPos) {
        Biome biome = storage.getBiomeForNoiseGen(chunkPos.getStartX() + CHUNK_CENTER_OFFSET, 64, chunkPos.getStartZ() + CHUNK_CENTER_OFFSET);
        return biome;
    }
}