package com.njdaeger.cci.config;


import com.njdaeger.cci.interfaces.IRegistryEntryReference;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.njdaeger.cci.CustomCreativeInventory.LOGGER;

public class CciItemGroup {

    private final String displayName;
    private final String registryKey;
    private final String icon;
    private final List<CciItemGroupEntry> entries;

    public CciItemGroup(String displayName, String registryKey, String icon, List<CciItemGroupEntry> entries) {
        this.displayName = displayName;
        this.registryKey = registryKey;
        this.icon = icon;
        this.entries = entries;
    }

    public String getRegistryKeyName() {
        return registryKey;
    }

    public RegistryKey<ItemGroup> getRegistryKey(String prefix) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of("cci", prefix + registryKey));
    }

    public Text getDisplayName() {
        return Text.of(displayName);
    }

    public Item getIcon() {
        return Registries.ITEM.get(Identifier.of(icon));
    }

    public List<CciItemGroupEntry> getEntries() {
        return entries;
    }

    public ItemGroup createItemGroup(TagKey<Item> tag) {
        return FabricItemGroup.builder()
                .displayName(getDisplayName())
                .icon(() -> new ItemStack(getIcon()))
                .entries((displayContext, entries) -> {
                    for (CciItemGroupEntry entry : getEntries()) {
                        var stack =  entry.getItem().getDefaultStack();

                        if (entry.getBlockState() != null)
                            stack.set(DataComponentTypes.BLOCK_STATE, new BlockStateComponent(entry.getBlockState()));

                        if (entry.getCustomModelData() != null)
                            stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, entry.getCustomModelData());

                        if (entry.getCustomName() != null)
                            stack.set(DataComponentTypes.ITEM_NAME, Text.of(entry.getCustomName()));

                        if (entry.getItemModel() != null)
                            stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(entry.getItemModel()));

                        ((IRegistryEntryReference<Item>)stack.getRegistryEntry()).customCreativeInventory$addTag(tag);
                        try {
                            entries.add(stack);
                        } catch (IllegalArgumentException e) {
                            LOGGER.warn("Item {} does not exist in the item registry.", entry.getItemName());
                        }
                    }
                }).build();
    }

}
