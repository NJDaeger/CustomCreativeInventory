package com.njdaeger.cci;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class CustomCreativeInventory implements ModInitializer {

    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CustomCreativeInventory.class);

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListener());
    }
}
