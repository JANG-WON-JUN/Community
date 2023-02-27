package com.jwj.community.config;

import com.jwj.community.web.argumentresolver.LoginMemberArgumentResolver;
import com.jwj.community.web.converter.enumconverter.converters.BoardSearchTypeConverter;
import com.jwj.community.web.converter.enumconverter.converters.BoardTypesConverter;
import com.jwj.community.web.converter.enumconverter.converters.SearchOrderConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(new BoardTypesConverter());
        registry.addConverter(new BoardSearchTypeConverter());
        registry.addConverter(new SearchOrderConverter());
    }
}
