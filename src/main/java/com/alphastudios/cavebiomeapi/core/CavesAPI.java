package com.alphastudios.cavebiomeapi.core;

import com.alphastudios.cavebiomeapi.core.api.CaveBiomeAPI;
import com.alphastudios.cavebiomeapi.core.registries.CaveBiomes;
import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//<>

/**
 * Hello, this API allows the developers to generate their own cave biomes in the world.
 * Special Thanks go to TelepathicGrunt, CorgiTaco and LudoCrypt who contributed with a lot of code and knowledge.
 *
 * @apiNote the API by itself it's in the {@link CaveBiomeAPI} class.
 */
public class CavesAPI implements ModInitializer {
    public static final String MOD_ID = "cavebiomeapi";

    @Override
    public void onInitialize() {
        Reflection.initialize(
                CaveBiomes.class,
                CaveBiomeAPI.class
        );
    }
}