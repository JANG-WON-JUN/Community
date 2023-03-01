package com.jwj.community.web.converter.enumconverter.converters;

import com.jwj.community.domain.enums.BoardTypes;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToBoardTypes;

@Component
public class BoardTypesConverter implements Converter<String, BoardTypes> {

    @Override
    public BoardTypes convert(@NonNull String source) {
        return stringToBoardTypes(source);
    }
}
