package com.jwj.community.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Sex {
    MALE, FEMALE;

    @JsonCreator
    public static Sex parse(String sex) {
        return switch (sex.toUpperCase()){
            case "M" -> MALE;
            case "F" -> FEMALE;
            default -> null;
        };
    }
}
