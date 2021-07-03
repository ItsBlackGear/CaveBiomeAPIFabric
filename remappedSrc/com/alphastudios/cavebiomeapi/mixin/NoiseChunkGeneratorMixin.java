package com.alphastudios.cavebiomeapi.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//<>

/**
 * @author TelepathicGrunt
 */
@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {
    @ModifyArg(method = "populateEntities(Lnet/minecraft/world/ChunkRegion;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ChunkRegion;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"))
    public BlockPos cba$populateSurfaceEntities(BlockPos pos) {
        return new BlockPos(pos.getX(), 64, pos.getZ());
    }
}