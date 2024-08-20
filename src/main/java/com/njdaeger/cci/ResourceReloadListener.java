package com.njdaeger.cci;

import com.google.gson.Gson;
import com.njdaeger.cci.config.CciConfig;
import com.njdaeger.cci.config.CciItemGroupEntry;
import com.njdaeger.cci.interfaces.IRegistryEntryReference;
import com.njdaeger.cci.interfaces.ISimpleRegistryInjector;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static com.njdaeger.cci.CustomCreativeInventory.*;

public class ResourceReloadListener implements IdentifiableResourceReloadListener {

    private static final Gson GSON = new Gson();
    private static final List<RegistryKey<ItemGroup>> customGroups = new ArrayList<>();
    private static final List<TagKey<Item>> customTags = new ArrayList<>();

    public ResourceReloadListener() {
        LOGGER.info("ResourceReloadListener initializing");
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("cci", "cci-resource-reload-listener");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> null, prepareExecutor)
            .thenCompose(synchronizer::whenPrepared)
            .thenAcceptAsync(v -> {

                LOGGER.info("Reloading inventories...");
                LOGGER.info("Clearing custom groups [{}]", customGroups.size());

                if (!customGroups.isEmpty()) {
                    customGroups.forEach(((ISimpleRegistryInjector<ItemGroup>)Registries.ITEM_GROUP)::customCreativeInventory$removeKey);
                    customGroups.forEach(key -> {
                        ((ISimpleRegistryInjector<ItemGroup>)Registries.ITEM_GROUP).customCreativeInventory$removeKey(key);
                        Registries.ITEM.streamEntries().filter(entry -> entry instanceof IRegistryEntryReference).map(entry -> (IRegistryEntryReference<Item>)entry).forEach(entry -> entry.customCreativeInventory$removeTag(customTags));
                    });
                    customGroups.clear();
                    customTags.clear();
                }

                manager.streamResourcePacks().forEach(pack -> {

                    var cciDefinition = pack.openRoot("cci.json");
                    if (cciDefinition != null) {

                        LOGGER.info("Found cci.json in {}", pack.getId());

                        try (final var cciStream = cciDefinition.get()){

                            var configString = new String(cciStream.readAllBytes());
                            CciConfig config;

                            try {
                                config = GSON.fromJson(configString, CciConfig.class);
                            } catch (Exception e) {
                                LOGGER.error("Failed to parse cci.json in {}", pack.getId());
                                LOGGER.error(e.getMessage());
                                return;
                            }

                            config.getItemGroups().forEach(group -> {

                                var key = group.getRegistryKey(customGroups.size());
                                LOGGER.info("Adding custom group: {}", key.toString());

                                var tag = TagKey.of(RegistryKeys.ITEM, Identifier.of("cci", customGroups.size() + group.getRegistryKeyName()));
                                ((ISimpleRegistryInjector<ItemGroup>)Registries.ITEM_GROUP).customCreativeInventory$addKey(key, group.createItemGroup(tag));

                                customTags.add(tag);
                                customGroups.add(key);
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                var lookup = BuiltinRegistries.createWrapperLookup();
                var itemGroupImpl = lookup.getOptionalWrapper(RegistryKeys.ITEM_GROUP);
                var bannerPatternImpl = lookup.getOptionalWrapper(RegistryKeys.BANNER_PATTERN);

                ItemGroups.updateDisplayContext(FeatureFlags.VANILLA_FEATURES, false, RegistryWrapper.WrapperLookup.of(Stream.of(itemGroupImpl.get(), bannerPatternImpl.get())));

                LOGGER.info("Reloading inventories complete.");

            });
    }
}
