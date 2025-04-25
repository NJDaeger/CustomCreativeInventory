package com.njdaeger.cci.mixin;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ItemGroup.class)
public abstract class ItemGroupMixin {

    @Shadow
    private Collection<ItemStack> displayStacks;

    @Shadow
    private Set<ItemStack> searchTabStacks;

    @Final
    @Shadow
    private ItemGroup.EntryCollector entryCollector;

    /**
     * @author njdaeger
     * @reason The fabric item group should be used
     */
    @Overwrite
    public void updateEntries(ItemGroup.DisplayContext displayContext) {
        var impl = new FabricItemGroupEntries(displayContext, new ArrayList<>(ItemStackSet.create()), new ArrayList<>(ItemStackSet.create()));
        Registries.ITEM_GROUP.getKey((ItemGroup) (Object) this).orElseThrow(() ->  new IllegalStateException("Unregistered creative tab: " + this));
        this.entryCollector.accept(displayContext, impl);
        this.displayStacks = impl.getDisplayStacks();
        this.searchTabStacks = new HashSet<>(impl.getSearchTabStacks());
    }

}
