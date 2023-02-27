package com.jwj.community.web.converter.enumconverter.converters;

import com.jwj.community.domain.enums.BoardTypes;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToBoardTypes;

public class BoardTypesConverter implements Converter<String, BoardTypes> {

    @Override
    public BoardTypes convert(@NonNull String source) {
        return stringToBoardTypes(source);
    }
}
