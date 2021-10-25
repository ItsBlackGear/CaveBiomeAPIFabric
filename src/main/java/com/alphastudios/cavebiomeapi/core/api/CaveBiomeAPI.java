package com.alphastudios.cavebiomeapi.core.api;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.alphastudios.cavebiomeapi.core.registries.CaveBiomes;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

//<>

/**
 * Special Thanks to TelepathicGrunt and LudoCrypt!
 */
public class CaveBiomeAPI {
    private static MultiNoiseBiomeSource caveBiomeSource;

    /**
     * Initializes the cave generation into the biome provider.
     *
     * @implNote This initialization calls the biome provider seed and the cave biome size.
     *
     * @param seed the biome provider seed
     *
     * @see com.alphastudios.cavebiomeapi.mixin.VanillaLayeredBiomeSourceMixin#cba$initialize(long, boolean, boolean, Registry, CallbackInfo)
     */
    public static void initializeCaveBiomes(Registry<Biome> biomeRegistry, long seed) {
    	caveBiomeSource = CaveLayer.create(biomeRegistry, seed);
    }

    /**
     * Injects the CaveBiome generation into a biomeSource
     *
     * @param surfaceBiomes the generated surface biomes
     * @param x the {@link net.minecraft.world.biome.source.BiomeSource#getBiomeForNoiseGen(int, int, int)} x value
     * @param y the {@link net.minecraft.world.biome.source.BiomeSource#getBiomeForNoiseGen(int, int, int)} y value
     * @param z the {@link net.minecraft.world.biome.source.BiomeSource#getBiomeForNoiseGen(int, int, int)} z value
     *
     * @return the CaveBiomes injected into the biomeSource
     * @see com.alphastudios.cavebiomeapi.mixin.VanillaLayeredBiomeSourceMixin#getBiomeForNoiseGen(int, int, int)
     */
    public static Biome injectCaveBiomes(Biome surfaceBiomes, int x, int y, int z) {
        if (y <= BiomeCoords.fromBlock(12) && y != BiomeCoords.fromBlock(0)) {
            return caveBiomeSource.getBiomeForNoiseGen(x, 0, z);
        }
        return surfaceBiomes;
    }

    /**
     * Injects a CaveBiome into the biomeSource
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
     * Injects a CaveBiome into the biomeSource
     *
     * @param biome the biome for injection
     * @param noise the mixed noise point used for generation
     */
    public static void addCaveBiome(Biome biome, Biome.MixedNoisePoint noise) {
        if (biome == null || BuiltinRegistries.BIOME.getKey(biome).isEmpty()) {
            throw new NullPointerException("CaveBiomeAPI's addCaveBiome method must take a registered biome. Null or unregistered biomes will be rejected.");
        }
        addCaveBiome(BuiltinRegistries.BIOME.getKey(biome).get(), noise);
    }

    /**
     * Example of injection for Cave Biomes into the biomeSource
     *
     * @see #addCaveBiome(Biome, Biome.MixedNoisePoint)
     */
    public static void addDefaultCaves() {
		CaveBiomeAPI.addCaveBiome(CaveBiomes.CAVE, new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		CaveBiomeAPI.addCaveBiome(BiomeKeys.LUSH_CAVES, new Biome.MixedNoisePoint(0.2F, 0.545F, 0.0F, 0.25F, 0.345F));
		CaveBiomeAPI.addCaveBiome(BiomeKeys.DRIPSTONE_CAVES, new Biome.MixedNoisePoint(-0.15F, 0.2F, 0.0F, -0.375F, -0.2F));
    }

    static {
        addDefaultCaves();
    }
}