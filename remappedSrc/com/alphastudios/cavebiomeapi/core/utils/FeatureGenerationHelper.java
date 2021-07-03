package com.alphastudios.cavebiomeapi.core.utils;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.List;
import java.util.function.Supplier;

//<>

/**
 * @author TelepathicGrunt
 */
public class FeatureGenerationHelper {
    /**
     * Will not spawn any structure and instead, only features.
     */
    public static void generateOnlyFeatures(Biome biome, ChunkGenerator generator, ChunkRegion region, long seed, ChunkRandom rand, BlockPos pos) {
        List<List<Supplier<ConfiguredFeature<?, ?>>>> list = biome.getGenerationSettings().getFeatures();
        for (int generationStageIndex = 0; generationStageIndex < GenerationStep.Feature.values().length; ++generationStageIndex) {
            int featureIndex = 1001; // offset index by 1001 so decorators for features do not exactly line up with features on surface biomes.
            if (list.size() > generationStageIndex) {
                for (Supplier<ConfiguredFeature<?, ?>> supplier : list.get(generationStageIndex)) {
                    ConfiguredFeature<?, ?> configuredFeature = supplier.get();
                    rand.setPopulationSeed(seed, featureIndex, generationStageIndex);

                    try {
                        configuredFeature.generate(region, generator, rand, pos);
                    } catch (Exception exception) {
                        CrashReport crashReport = CrashReport.create(exception, "Feature placement");
                        crashReport.addElement("Feature")
                                .add("Id", Registry.FEATURE.getKey(configuredFeature.feature))
                                .add("Config", configuredFeature.config)
                                .add("Description", configuredFeature.feature.toString());
                        throw new CrashException(crashReport);
                    }

                    ++featureIndex;
                }
            }
        }
    }
}