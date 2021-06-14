package com.alphastudios.cavebiomeapi.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.alphastudios.cavebiomeapi.mixin.MultiNoiseBiomeSourceAccessor;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

//<>

/**
 * @author LudoCrypt
 */
public class CaveLayer {
	public static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> CAVE_BIOMES = new HashMap<>();
	public static final List<Biome> CAVE_BIOME_LIST = new ArrayList<>();

	public static MultiNoiseBiomeSource create(Registry<Biome> biomeRegistry, long seed) {
		CAVE_BIOME_LIST.addAll(CAVE_BIOMES.keySet().stream().map(biomeRegistry::get).collect(Collectors.toList()));
		return CENTER_BIOME_SOURCE.getBiomeSource(biomeRegistry, seed);
	}

	public static void addCaveBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint noise) {
		Preconditions.checkNotNull(biome, "biome is null");
		Preconditions.checkNotNull(noise, "noise is null");
		CAVE_BIOMES.put(biome, noise);
	}

	public static final MultiNoiseBiomeSource.Preset CENTER_BIOME_SOURCE = new MultiNoiseBiomeSource.Preset(new Identifier("cavebiomes", "cave_biome_source"), (preset, registry, long_) -> {
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomes = new ArrayList<>();
		CAVE_BIOMES.forEach((biomeKey, noisePoint) -> {
			Biome biome = registry.getOrThrow(biomeKey);
			biomes.add(Pair.of(noisePoint, () -> biome));
		});
		return MultiNoiseBiomeSourceAccessor.createMultiNoiseBiomeSource(long_, biomes, Optional.of(Pair.of(registry, preset)));
	});
}