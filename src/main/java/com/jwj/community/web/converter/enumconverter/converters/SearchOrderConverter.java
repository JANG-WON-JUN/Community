package com.jwj.community.web.converter.enumconverter.converters;

import com.jwj.community.web.enums.SearchOrder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.jwj.community.web.converter.enumconverter.EnumConverterService.stringToSearchOrder;

@Component
public class SearchOrderConverter implements Converter<String, SearchOrder> {

    @Override
    public SearchOrder convert(@NonNull String source) {
        return stringToSearchOrder(source);
    }
}
