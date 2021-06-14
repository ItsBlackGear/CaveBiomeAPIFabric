package com.alphastudios.cavebiomeapi.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

//<>

@Mixin(HorizontalVoronoiBiomeAccessType.class)
public class HorizontalVoronoiBiomeAccessTypeMixin {
    /**
     * @author CorgiTaco
     * @reason by default it locates the biome on a 2D map, this method modifies that and allows the BiomeAccessType to return the Y Axis as well.
     */
    @Overwrite
    public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
        return VoronoiBiomeAccessType.INSTANCE.getBiome(seed, x, y, z, storage);
    }
}