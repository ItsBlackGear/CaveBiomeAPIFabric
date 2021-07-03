package com.alphastudios.cavebiomeapi.mixin;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//<>

@Mixin(BiomeColors.class)
public abstract class BiomeColorsMixin {
    @Shadow @Final public static ColorResolver GRASS_COLOR;
    @Shadow @Final public static ColorResolver FOLIAGE_COLOR;
    @Shadow @Final public static ColorResolver WATER_COLOR;

    @Shadow
    private static int getColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
        return world.getColor(pos, resolver);
    }

    @Inject(method = "getGrassColor", at = @At("HEAD"), cancellable = true)
    private static void getGrassColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(getColor(world, pos.withY(world.getTopY()), GRASS_COLOR));
    }

    @Inject(method = "getFoliageColor", at = @At("HEAD"), cancellable = true)
    private static void getFoliageColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(getColor(world, pos.withY(world.getTopY()), FOLIAGE_COLOR));
    }

    @Inject(method = "getWaterColor", at = @At("HEAD"), cancellable = true)
    private static void getWaterColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(getColor(world, pos.withY(world.getTopY()), WATER_COLOR));
    }
}