package com.jwj.community.web.converter.enumconverter.converters;

import com.jwj.community.web.enums.BoardSearchType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToBoardSearchType;

public class BoardSearchTypeConverter implements Converter<String, BoardSearchType> {

    @Override
    public BoardSearchType convert(@NonNull String source) {
        return stringToBoardSearchType(source);
    }
}
