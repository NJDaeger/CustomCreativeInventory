package com.njdaeger.cci.mixin;

import com.njdaeger.cci.interfaces.IRegistryEntryReference;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(targets = "net.minecraft.registry.entry.RegistryEntry$Reference")
public abstract class RegistryEntryMixin<T> implements IRegistryEntryReference<T> {

    @Shadow
    private T value;

    @Shadow
    private Set<TagKey<T>> tags;

    @Shadow
    abstract void setRegistryKey(RegistryKey<T> registryKey);

    @Shadow
    abstract void setTags(Collection<TagKey<T>> tags);

    @Shadow public abstract Stream<TagKey<T>> streamTags();

    @Override
    public void customCreativeInventory$overrideRegistryKey(RegistryKey<T> registryKey) {
        setRegistryKey(registryKey);
    }

    @Override
    public void customCreativeInventory$overrideValue(T value) {
        this.value = value;
    }

    @Override
    public void customCreativeInventory$addTag(TagKey<T> tag) {
        var curTags = streamTags().toList();
        var newTags = new ArrayList<>(curTags);
        newTags.add(tag);
        setTags(newTags);
    }

    @Override
    public void customCreativeInventory$removeTag(Collection<TagKey<T>> tagsToRemove) {
        var curTags = streamTags().toList();
        var newTags = new ArrayList<>(curTags);
        newTags.removeAll(tagsToRemove);
        setTags(newTags);
    }
}
