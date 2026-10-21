package com.jonas.eurder.domain;

public enum UrgencyLevel {
    STOCK_LOW,
    STOCK_MEDIUM,
    STOCK_HIGH;

    public static UrgencyLevel fromStock(int amount) {
        if (amount < 5) {
            return STOCK_LOW;
        }
        if (amount < 10) {
            return STOCK_MEDIUM;
        }
        return STOCK_HIGH;
    }
}
