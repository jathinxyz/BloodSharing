package com.bloodmatcher.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * ABO and Rh blood group representation with strict format validation.
 */
public enum BloodGroup {
    O_NEG("O-"),
    O_POS("O+"),
    A_NEG("A-"),
    A_POS("A+"),
    B_NEG("B-"),
    B_POS("B+"),
    AB_NEG("AB-"),
    AB_POS("AB+");

    private final String displayValue;

    BloodGroup(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator
    public static BloodGroup fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Blood group must not be blank");
        }
        String normalized = value.trim().toUpperCase().replace("POSITIVE", "+").replace("NEGATIVE", "-");
        for (BloodGroup bg : values()) {
            if (bg.name().equalsIgnoreCase(normalized) || bg.displayValue.equalsIgnoreCase(normalized)) {
                return bg;
            }
        }
        throw new IllegalArgumentException("Unsupported blood group: '" + value + "'. Supported: " 
                + Arrays.toString(Arrays.stream(values()).map(BloodGroup::getDisplayValue).toArray()));
    }
}
