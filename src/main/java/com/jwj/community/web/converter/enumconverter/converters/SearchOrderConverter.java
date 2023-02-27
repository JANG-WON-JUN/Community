package com.jwj.community.web.converter.enumconverter.converters;

import com.jwj.community.web.enums.SearchOrder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToSearchOrder;

public class SearchOrderConverter implements Converter<String, SearchOrder> {

    @Override
    public SearchOrder convert(@NonNull String source) {
        return stringToSearchOrder(source);
    }
}
