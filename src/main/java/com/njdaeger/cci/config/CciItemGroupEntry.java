package com.njdaeger.cci.config;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CciItemGroupEntry {

    private final String itemName;

    public CciItemGroupEntry(String itemName) {
        this.itemName = itemName;
    }

    @NotNull
    public Item getItem() {
        return Registries.ITEM.get(Identifier.of(itemName));
    }

    public String getItemName() {
        return itemName;
    }

}
