package com.jwj.community.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SearchOrder {
    NEW, VIEW, LIKE;

    @JsonCreator
    public static SearchOrder parse(String searchOrder) {
        return switch (searchOrder.toUpperCase()){
            case "V" -> VIEW;
            case "L" -> LIKE;
            default -> NEW;
        };
    }
}
