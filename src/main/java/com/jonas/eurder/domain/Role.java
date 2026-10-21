package com.jonas.eurder.domain;

import java.util.List;

public enum Role {
    ADMIN(List.of(Feature.ADD_ITEM, Feature.UPDATE_ITEM, Feature.VIEW_ALL_CUSTOMERS, Feature.VIEW_SINGLE_CUSTOMER, Feature.VIEW_SHIPPING_TODAY, Feature.VIEW_STOCK_OVERVIEW)),
    CUSTOMER(List.of(Feature.ORDER_ITEM, Feature.VIEW_REPORT_OF_ORDERS, Feature.REORDER));

    private final List<Feature> features;

    Role(List<Feature> features) {
        this.features = features;
    }

    public boolean containsFeature(Feature feature) {
        return features.contains(feature);
    }
}
