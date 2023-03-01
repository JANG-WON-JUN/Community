package com.jwj.community.config.security.utils.code;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class JwtParamFactory {

    public static Stream<Arguments> getTokenParam(String jwtToken){
        return Stream.of(
                Arguments.of(jwtToken));
    }

    public static String test(){
        return "test";
    }
}
