package com.njdaeger.cci.config;

import java.util.List;

public class CciConfig {

    private final List<CciItemGroup> itemGroups;

    public CciConfig(List<CciItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    public List<CciItemGroup> getItemGroups() {
        return itemGroups;
    }

}
