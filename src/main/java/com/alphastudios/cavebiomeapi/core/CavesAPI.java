package com.alphastudios.cavebiomeapi.core;

import com.alphastudios.cavebiomeapi.core.api.CaveBiomeAPI;
import com.alphastudios.cavebiomeapi.core.registries.CaveBiomes;
import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//<>

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