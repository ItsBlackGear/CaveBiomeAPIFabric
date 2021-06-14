package com.alphastudios.cavebiomeapi.core.api;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.alphastudios.cavebiomeapi.core.registries.CaveBiomes;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionType;

//<>

public class CaveBiomeAPI {
    private static MultiNoiseBiomeSource caveBiomeSource;

    /**
     * Initializes the cave generation into the biome provider.
     *
     * @implNote This initialization calls the biome provider seed and the cave biome size.
     *
     * @param seed the biome provider seed
     *
     * @see com.alphastudios.cavebiomeapi.mixin.VanillaLayeredBiomeSourceMixin#initialize(long, boolean, boolean, Registry, CallbackInfo)
     */
    public static void initializeCaveBiomes(Registry<Biome> biomeRegistry, long seed) {
    	caveBiomeSource = CaveLayer.create(biomeRegistry, seed);
    }

    /**
     * Injects the CaveBiome generation into a biomeProvider
     *
     * @implNote we don't make cave biomes spawn at y0 because otherwise entities and structures don't spawn.
     *
     * @param surfaceBiomes the generated surface biomes
     * @param biomeRegistry the biome registry given in the biome provider
     * @param x the {@link net.minecraft.world.biome.source.BiomeSource#getBiomeForNoiseGen(int, int, int)} x value
     * @param y the {@link net.minecraft.world.biome.source.BiomeSource#getBiomeForNoiseGen(int, int, int)} y value
     * @param z the {@link net.minecraft.world.biome.source.BiomeSource#getBiomeForNoiseGen(int, int, int)} z value
     *
     * @return the CaveBiomes injected into the biomeProvider
     * @see com.alphastudios.cavebiomeapi.mixin.VanillaLayeredBiomeSourceMixin#getBiomeForNoiseGen(int, int, int)
     */
    public static Biome injectCaveBiomes(Biome surfaceBiomes, Registry<Biome> biomeRegistry, int x, int y, int z) {
        if (y <= 12) {
            return sample(biomeRegistry, x, z);
        }
        return surfaceBiomes;
    }

    /**
     * Don't use the vanilla layer method of func_242936_a.
     * It's bugged and checks the wrong registry first to resolve the biome id which can lead to crashes.
     *
     * @param dynamicBiomeRegistry - the registry vanilla should've grabbed the biome from first
     * @param x - position on x axis in world
     * @param z - position on z axis in world
     *
     * @return the dynamicregistry instance of the biome if done properly
     */
    public static Biome sample(Registry<Biome> dynamicBiomeRegistry, int x, int z) {
    	return caveBiomeSource.getBiomeForNoiseGen(x, 0, z);
    }

    /**
     * Injects a CaveBiome into the biomeLayer
     *
     * @see #addDefaultCaves()
     *
     * @param biome the biome for injection
     */
    public static void addCaveBiome(Biome biome, Biome.MixedNoisePoint noise) {
        if (biome == null || BuiltinRegistries.BIOME.getKey(biome).isEmpty()) {
            throw new NullPointerException("CaveBiomeAPI's addCaveBiome method must take a registered biome. Null or unregistered biomes will be rejected.");
        }
        // Store the key as we will get the correct biome instance when the biome source is created.
        addCaveBiome(BuiltinRegistries.BIOME.getKey(biome).get(), noise);
    }

    /**
     * Injects a CaveBiome into the biomeLayer
     *
     * @see #addDefaultCaves()
     *
     * @param biome the biome for injection
     * @param noise the mixed noise point used for generation
     */
    public static void addCaveBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint noise) {
        if (biome == null) {
            throw new NullPointerException("CaveBiomeAPI's addCaveBiome method must take a registered biome. Null or unregistered biomes will be rejected.");
        }
        // Store the key as we will get the correct biome instance when the biome source is created.
        CaveLayer.addCaveBiome(biome, noise);
    }

    /**
     * Injects the selected CaveBiomes into the biomeLayer
     *
     * @see #addCaveBiome(Biome, Biome.MixedNoisePoint)
     */
    public static void addDefaultCaves() {
		CaveBiomeAPI.addCaveBiome(CaveBiomes.CAVE, new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		CaveBiomeAPI.addCaveBiome(BiomeKeys.LUSH_CAVES, new Biome.MixedNoisePoint(-0.2F, 0.2F, 0.325F, -0.15F, 0.0F));
		CaveBiomeAPI.addCaveBiome(BiomeKeys.DRIPSTONE_CAVES, new Biome.MixedNoisePoint(0.0F, -0.325F, -0.275F, 0.2F, 0.0F));
    }

    static {
        addDefaultCaves();
    }
}