package com.njdaeger.cci.config;

import net.minecraft.component.type.CustomModelDataComponent;

import java.util.List;

public class CciItemGroupEntryCustomModelData {

    private List<Float> floats;
    private List<Boolean> flags;
    private List<String> strings;
    private List<Integer> colors;

    public CustomModelDataComponent toCustomModelDataComponent() {
        var defaultData = CustomModelDataComponent.DEFAULT;

        if (floats == null) floats = defaultData.floats();
        if (flags == null) flags = defaultData.flags();
        if (strings == null) strings = defaultData.strings();
        if (colors == null) colors = defaultData.colors();

        return new CustomModelDataComponent(floats, flags, strings, colors);
    }

}
