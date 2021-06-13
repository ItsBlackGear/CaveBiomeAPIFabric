package com.alphastudios.cavebiomeapi.mixin.structure;

import com.alphastudios.cavebiomeapi.core.api.CaveLayer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

//<>

@Mixin(OceanMonumentFeature.class)
public class OceanMonumentFeatureMixin {
    @Redirect(method = "shouldStartAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeSource;getBiomesInArea(IIII)Ljava/util/Set;"))
    private Set<Biome> getSurfaceBiomes(BiomeSource source, int x, int y, int z, int radius) {
        Set<Biome> biomeSet = source.getBiomesInArea(x, y, z, radius);
        biomeSet.removeIf(CaveLayer.caveBiomes::contains);
        return biomeSet;
    }
}