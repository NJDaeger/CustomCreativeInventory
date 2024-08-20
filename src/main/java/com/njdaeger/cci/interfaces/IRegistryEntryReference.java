package com.njdaeger.cci.interfaces;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.Collection;

public interface IRegistryEntryReference<T> {

    default void customCreativeInventory$overrideRegistryKey(RegistryKey<T> registryKey) {}

    default void customCreativeInventory$overrideValue(T value) {}

    default void customCreativeInventory$addTag(TagKey<T> tag) {}

    default void customCreativeInventory$removeTag(Collection<TagKey<T>> tagsToRemove) {}

}
