package com.njdaeger.cci.config;

import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CciItemGroupEntry {

    private String itemName;
    private String customName;
    private Map<String, String> blockState;
    private CciItemGroupEntryCustomModelData customModelData;
    private String itemModel;

    @NotNull
    public Item getItem() {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalStateException("Item name cannot be null or blank");
        }
        return Registries.ITEM.get(Identifier.of(itemName));
    }

    public Map<String, String> getBlockState() {
        return blockState;
    }

    public CustomModelDataComponent getCustomModelData() {
        if (customModelData == null) return null;
        return customModelData.toCustomModelDataComponent();
    }

    public String getItemModel() {
        return itemModel;
    }

    public String getCustomName() {
        return customName;
    }

    public String getItemName() {
        return itemName;
    }

}
