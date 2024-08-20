package com.njdaeger.cci.interfaces;

import net.minecraft.registry.RegistryKey;

public interface ISimpleRegistryInjector<T> {

    default void customCreativeInventory$removeKey(RegistryKey<T> key) {}

    default void customCreativeInventory$addKey(RegistryKey<T> key, T value) {}

}
