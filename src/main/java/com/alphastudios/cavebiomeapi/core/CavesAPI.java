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
 * Hello, this API allows the developers to generate their own cave biomes in the world. if you're looking for the API:
 *
 * @see CaveBiomeAPI
 */
public class CavesAPI implements ModInitializer {
    public static final String MOD_ID = "cavebiomes";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        Reflection.initialize(
                CaveBiomes.class,
                CaveBiomeAPI.class
        );
    }
}