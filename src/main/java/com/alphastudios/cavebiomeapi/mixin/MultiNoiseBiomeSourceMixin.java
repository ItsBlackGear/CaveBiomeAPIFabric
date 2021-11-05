package com.alphastudios.cavebiomeapi.mixin;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

//<>

/**
 * @author SalveMundiProd
 */
@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
	   @Shadow @Final private DoublePerlinNoiseSampler temperatureNoise;
	   @Shadow @Final private DoublePerlinNoiseSampler humidityNoise;
	   @Shadow @Final private DoublePerlinNoiseSampler altitudeNoise;
	   @Shadow @Final private DoublePerlinNoiseSampler weirdnessNoise;
	   @Shadow @Final private List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
	   @Shadow @Final private boolean threeDimensionalSampling;
	   @Shadow @Final private long seed;
	   @Shadow @Final private Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance;
	
	@Inject(at = @At("RETURN"), method = "getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;", cancellable = true)
	public void change2DSamplingLocation(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
		if (cir.getReturnValue().getCategory() != Biome.Category.NETHER && cir.getReturnValue().getCategory() != Biome.Category.THEEND) {
			int i = this.threeDimensionalSampling ? biomeY : 128;
		      Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample((double)biomeX, (double)i, (double)biomeZ), (float)this.humidityNoise.sample((double)biomeX, (double)i, (double)biomeZ), (float)this.altitudeNoise.sample((double)biomeX, (double)i, (double)biomeZ), (float)this.weirdnessNoise.sample((double)biomeX, (double)i, (double)biomeZ), 0.0F);
		      cir.setReturnValue(this.biomePoints.stream().min(Comparator.comparing((pair) -> {
		         return ((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint);
		      })).map(Pair::getSecond).map(Supplier::get).orElse(BuiltinBiomes.THE_VOID));
		}
	}
}
