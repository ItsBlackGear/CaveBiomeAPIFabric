package com.alphastudios.cavebiomeapi.core.api;

import com.alphastudios.cavebiomeapi.core.CavesAPI;
import com.alphastudios.cavebiomeapi.core.registries.CaveBiomes;
import com.alphastudios.cavebiomeapi.mixin.LayerAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//<>

public class CaveBiomeAPI {
    private static BiomeLayerSampler caveLayer;

    /**
     * Initializes the cave generation into the biome provider.
     *
     * @implNote This initialization calls the biome provider seed and the cave biome size.
     *
     * @param seed the biome provider seed
     * @param size the cave biome size
     *
     * @see com.alphastudios.cavebiomeapi.mixin.VanillaLayeredBiomeSourceMixin#initialize(long, boolean, boolean, Registry, CallbackInfo)
     */
    public static void initializeCaveBiomes(Registry<Biome> biomeRegistry, long seed, int size) {
        caveLayer = CaveLayer.generateCaveLayers(biomeRegistry, seed, size);
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
        if (y <= 12 && y >= DimensionType.MIN_HEIGHT + 1) {
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
        int resultBiomeID = ((LayerAccessor)caveLayer).getCaveSampler().sample(x, z);
        Biome biome = dynamicBiomeRegistry.get(resultBiomeID);
        if (biome == null) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + resultBiomeID));
            } else {
                // Spawn ocean if we can't resolve the biome from the layers.
                RegistryKey<Biome> backupBiomeKey = BuiltinBiomes.fromRawId(0);
                CavesAPI.LOGGER.warn("Unknown biome id: ${}. Will spawn ${} instead.", resultBiomeID, backupBiomeKey.getValue());
                return dynamicBiomeRegistry.get(backupBiomeKey);
            }
        } else {
            return biome;
        }
    }

    /**
     * Injects a CaveBiome into the biomeLayer
     *
     * @see #addDefaultCaves()
     *
     * @param biome the biome for injection
     */
    public static void addCaveBiome(Biome biome) {
        if (biome == null || BuiltinRegistries.BIOME.getKey(biome).isEmpty()) {
            throw new NullPointerException("CaveBiomeAPI's addCaveBiome method must take a registered biome. Null or unregistered biomes will be rejected.");
        }
        // Store the key as we will get the correct biome instance when the biome source is created.
        CaveLayer.caveBiomeKeys.add(BuiltinRegistries.BIOME.getKey(biome).get());
    }

    /**
     * Injects a CaveBiome into the biomeLayer
     *
     * @see #addDefaultCaves()
     *
     * @param biome the biome for injection
     */
    public static void addCaveBiome(RegistryKey<Biome> biome) {
        if (biome == null) {
            throw new NullPointerException("CaveBiomeAPI's addCaveBiome method must take a registered biome. Null or unregistered biomes will be rejected.");
        }
        // Store the key as we will get the correct biome instance when the biome source is created.
//        CaveLayer.caveBiomeKeys.add(BuiltinRegistries.BIOME.getRawId(biome));
        CaveLayer.caveBiomeKeys.add(biome);
    }

    /**
     * Injects the selected CaveBiomes into the biomeLayer
     *
     * @see #addCaveBiome(Biome)
     */
    public static void addDefaultCaves() {
        CaveBiomeAPI.addCaveBiome(CaveBiomes.CAVE);
        CaveBiomeAPI.addCaveBiome(BiomeKeys.LUSH_CAVES);
        CaveBiomeAPI.addCaveBiome(BiomeKeys.DRIPSTONE_CAVES);
    }

    static {
        addDefaultCaves();
    }
}