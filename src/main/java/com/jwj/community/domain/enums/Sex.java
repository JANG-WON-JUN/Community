package com.jwj.community.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToSex;

public enum Sex {
    MALE, FEMALE;

    @JsonCreator
    public static Sex parse(String source) {
        return stringToSex(source);
    }
}
