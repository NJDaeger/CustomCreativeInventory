package com.njdaeger.cci.mixin;

import com.mojang.serialization.Lifecycle;
import com.njdaeger.cci.interfaces.IRegistryEntryReference;
import com.njdaeger.cci.interfaces.ISimpleRegistryInjector;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> implements ISimpleRegistryInjector<T> {

    @Shadow
    @Final
    private ObjectList<RegistryEntry.Reference<T>> rawIdToEntry;

    @Shadow
    @Final
    private Reference2IntMap<T> entryToRawId;

    @Shadow
    @Final
    private Map<Identifier, RegistryEntry.Reference<T>> idToEntry;

    @Shadow
    @Final
    private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;

    @Shadow
    @Final
    private Map<T, RegistryEntry.Reference<T>> valueToEntry;

    @Shadow
    @Final
    private Map<RegistryKey<T>, RegistryEntryInfo> keyToEntryInfo;

    @Shadow
    private Map<T, RegistryEntry.Reference<T>> intrusiveValueToEntry;

    @Shadow
    private Lifecycle lifecycle;

    @Shadow
    public abstract RegistryEntryOwner<T> getEntryOwner();

    @Override
    public void customCreativeInventory$removeKey(RegistryKey<T> key) {
        var ref = this.keyToEntry.remove(key);
        if (ref == null) return;
        this.idToEntry.remove(key.getValue());
        this.valueToEntry.remove(ref.value());
        this.rawIdToEntry.remove(ref);
        this.entryToRawId.remove(ref.value());
        this.keyToEntryInfo.remove(key);
        if (this.intrusiveValueToEntry != null) {
            this.intrusiveValueToEntry.put(ref.value(), ref);
        }
        this.lifecycle = this.lifecycle.add(RegistryEntryInfo.DEFAULT.lifecycle());
    }

    @Override
    public void customCreativeInventory$addKey(RegistryKey<T> key, T value) {
        RegistryEntry.Reference<T> ref;
        if (this.intrusiveValueToEntry != null) {
            ref = this.intrusiveValueToEntry.remove(value);
        } else ref = this.keyToEntry.computeIfAbsent(key, k -> RegistryEntry.Reference.standAlone(getEntryOwner(), k));

        ((IRegistryEntryReference<T>) ref).customCreativeInventory$overrideRegistryKey(key);
        ((IRegistryEntryReference<T>) ref).customCreativeInventory$overrideValue(value);

        var info = RegistryEntryInfo.DEFAULT;
        this.keyToEntry.put(key, ref);
        this.idToEntry.put(key.getValue(), ref);
        this.valueToEntry.put(value, ref);
        int i = this.rawIdToEntry.size();
        this.rawIdToEntry.add(ref);
        this.entryToRawId.put(value, i);
        this.keyToEntryInfo.put(key, info);
        this.lifecycle = this.lifecycle.add(info.lifecycle());
    }
}
