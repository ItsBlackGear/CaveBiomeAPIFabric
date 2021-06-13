package com.alphastudios.cavebiomeapi.mixin;

import com.alphastudios.cavebiomeapi.core.api.CaveBiomeAPI;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//<>

@Mixin(VanillaLayeredBiomeSource.class)
public class VanillaLayeredBiomeSourceMixin {
    @Shadow @Final private BiomeLayerSampler biomeSampler;
    @Shadow @Final private Registry<Biome> biomeRegistry;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initialize(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes, Registry<Biome> biomeRegistry, CallbackInfo ci) {
        CaveBiomeAPI.initializeCaveBiomes(biomeRegistry, seed);
    }

    /**
     * @author BlackGear27
     */
    @Overwrite
    public Biome getBiomeForNoiseGen(int x, int y, int z) {
        Biome surfaceBiome = this.biomeSampler.sample(this.biomeRegistry, x, z);
        return CaveBiomeAPI.injectCaveBiomes(surfaceBiome, this.biomeRegistry, x, y, z);
    }
}