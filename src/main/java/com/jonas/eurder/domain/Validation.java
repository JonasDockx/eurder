package com.jonas.eurder.domain;

import java.math.BigDecimal;
import java.util.List;

public final class Validation {

    private Validation() {

    }

    public static String validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must be provided");
        }
        return value;
    }

    public static <T> T validateNotNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must be provided");
        }
        return value;
    }

    public static BigDecimal validateNotNegative(BigDecimal value, String fieldName) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " must be 0 or more");
        }
        return value;
    }

    public static int validateNotNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be 0 or more");
        }
        return value;
    }

    public static int validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be 0 or more");
        }
        return value;
    }

    public static <T> List<T> validateNotEmpty(List<T> values, String fieldName) {
        validateNotNull(values, fieldName);
        if (values.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must contain at least one element");
        }
        return values;
    }

}
