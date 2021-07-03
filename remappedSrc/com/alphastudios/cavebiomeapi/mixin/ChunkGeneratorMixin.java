package com.alphastudios.cavebiomeapi.mixin;

import com.alphastudios.cavebiomeapi.core.api.CaveLayer;
import com.alphastudios.cavebiomeapi.core.utils.FeatureGenerationHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//<>

/**
 * Special thanks to TelepathicGrunt that helped to fix not one but almost every issue with this code on it's experimental stage.
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Shadow @Final protected BiomeSource populationSource;

    @Redirect(method = "generateFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/world/biome/Biome;"))
    private Biome cba$generateSurfaceFeatures(BiomeSource source, ChunkPos chunkPos) {
        return source.getBiomeForNoiseGen(BiomeCoords.fromChunk(chunkPos.x) + BiomeCoords.fromBlock(8), 64, BiomeCoords.fromChunk(chunkPos.z) + BiomeCoords.fromBlock(8));
    }

    @Inject(method = "generateFeatures", at = @At("RETURN"), cancellable = true)
    private void cba$generateUndergroundFeatures(ChunkRegion region, StructureAccessor accessor, CallbackInfo ci) {
        ChunkPos chunkPos = region.getCenterPos();
        int x = chunkPos.getStartX();
        int z = chunkPos.getStartZ();
        BlockPos pos = new BlockPos(x, region.getBottomY(), z);
        Biome biome = this.populationSource.getBiomeForNoiseGen(BiomeCoords.fromChunk(chunkPos.x) + BiomeCoords.fromBlock(8), region.getBottomY() + 10, BiomeCoords.fromChunk(chunkPos.z) + BiomeCoords.fromBlock(8));
        if (!CaveLayer.CAVE_BIOME_LIST.contains(biome)) return;

        ChunkRandom chunkRandom = new ChunkRandom();
        long seed = chunkRandom.setPopulationSeed(region.getSeed(), x, z);

        try {
            FeatureGenerationHelper.generateOnlyFeatures(biome, (ChunkGenerator)(Object)this, region, seed, chunkRandom, pos);
        } catch (Exception exception) {
            CrashReport crashReport = CrashReport.create(exception, "Biome decoration");
            crashReport.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z).add("Seed", seed).add("Biome", biome);
            throw new CrashException(crashReport);
        }
    }

    /**
     * @author TelepathicGrunt
     */
    @Redirect(method = "setStructureStarts(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/structure/StructureManager;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomeForNoiseGen(Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/world/biome/Biome;"))
    private Biome cba$setStructureStarts(BiomeSource source, ChunkPos chunkPos) {
        return source.getBiomeForNoiseGen(BiomeCoords.fromChunk(chunkPos.x) + BiomeCoords.fromBlock(8), 64, BiomeCoords.fromChunk(chunkPos.z) + BiomeCoords.fromBlock(8));
    }
}