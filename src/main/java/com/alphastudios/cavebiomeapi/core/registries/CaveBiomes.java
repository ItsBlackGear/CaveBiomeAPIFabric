package com.alphastudios.cavebiomeapi.core.registries;

import com.alphastudios.cavebiomeapi.core.CavesAPI;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

//<>

public class CaveBiomes {
    public static final RegistryKey<Biome> CAVE = registerBiome("caves", createDefaultCaves());

    private static RegistryKey<Biome> registerBiome(String key, Biome biome) {
        Identifier identifier = new Identifier(CavesAPI.MOD_ID, key);
        BuiltinRegistries.add(BuiltinRegistries.BIOME, identifier, biome);
        return RegistryKey.of(Registry.BIOME_KEY, identifier);
    }

    public static Biome createDefaultCaves() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(spawnSettings);
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
        generationSettings.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addDefaultLakes(generationSettings);
        DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addDefaultOres(generationSettings);
        DefaultBiomeFeatures.addDefaultMushrooms(generationSettings);
        DefaultBiomeFeatures.addDefaultVegetation(generationSettings);
        DefaultBiomeFeatures.addSprings(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
        return new Biome.Builder().precipitation(Biome.Precipitation.RAIN).category(Biome.Category.UNDERGROUND).depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).effects(new BiomeEffects.Builder().waterColor(0x0d67bb).waterFogColor(0x0d67bb).fogColor(0x000000).skyColor(getSkyColorWithTemperatureModifier()).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(spawnSettings.build()).generationSettings(generationSettings.build()).build();
    }

    private static int getSkyColorWithTemperatureModifier() {
        float modifier = 0.8F / 3.0F;
        modifier = MathHelper.clamp(modifier, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - modifier * 0.05F, 0.5F + modifier * 0.1F, 1.0F);
    }
}