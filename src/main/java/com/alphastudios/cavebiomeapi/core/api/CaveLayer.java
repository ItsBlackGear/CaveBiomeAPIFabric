package com.alphastudios.cavebiomeapi.core.api;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

//<>

public class CaveLayer {
    public static final List<RegistryKey<Biome>> caveBiomeKeys = new ArrayList<>();
    public static final List<Biome> caveBiomes = new ArrayList<>(); // For biome spawning
    public static final Set<Biome> caveBiomeSet = new HashSet<>(); // For quick checking if a biome is a cave biome

    public static BiomeLayerSampler generateCaveLayers(Registry<Biome> biomeRegistry, long seed, int biomeSize) {
        // Clear set and list of cave biomes so we can get the correct biome instance from the world's dynamic registry.
        caveBiomes.clear();
        caveBiomeSet.clear();
        caveBiomes.addAll(caveBiomeKeys.stream().map(biomeRegistry::get).collect(Collectors.toList()));
        caveBiomeSet.addAll(caveBiomes);

        LongFunction<LayerSampleContext<CachingLayerSampler>> context = salt -> new CachingLayerContext(25, seed, salt);

        LayerFactory<CachingLayerSampler> factory = new CaveBiomeLayer(biomeRegistry, caveBiomes).create(context.apply(200L));

        for (int size = 0; size < biomeSize; size++) {
            if ((size + 2) % 3 != 0) {
                factory = ScaleLayer.NORMAL.create(context.apply(2001L + size), factory);
            } else {
                factory = ScaleLayer.NORMAL.create(context.apply(2000L + (size * 31L)), factory);
            }
        }

        return new BiomeLayerSampler(factory);
    }

    public static class CaveBiomeLayer implements InitLayer {
        public final Registry<Biome> dynamicBiomeRegistry;
        private final List<Biome> biomes;

        public CaveBiomeLayer(Registry<Biome> biomeRegistry, List<Biome> biomes) {
            this.dynamicBiomeRegistry = biomeRegistry;
            this.biomes = biomes;
        }

        @Override
        public int sample(LayerRandomnessSource context, int x, int y) {
            return this.dynamicBiomeRegistry.getRawId(this.biomes.get(context.nextInt(this.biomes.size())));
        }
    }
}