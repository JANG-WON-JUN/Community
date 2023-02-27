package com.jwj.community.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToSearchOrder;

public enum SearchOrder {
    NEW, VIEW, LIKE;

    @JsonCreator
    public static SearchOrder parse(String source) {
        return stringToSearchOrder(source);
    }
}
