package com.jwj.community.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToBoardTypes;

@Getter
@AllArgsConstructor
public enum BoardTypes {
    DAILY("일상 게시판"),
    DEV("개발 게시판");

    private String description;

    @JsonCreator
    public static BoardTypes parse(String source){
        return stringToBoardTypes(source);
    }
}
