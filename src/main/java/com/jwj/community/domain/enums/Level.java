package com.jwj.community.domain.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Level {

    LEVEL1(0, 100),
    LEVEL2(101, 200),
    LEVEL3(201, 300),
    LEVEL4(301, 400),
    LEVEL5(401, 500),
    LEVEL6(601, 700),
    LEVEL8(701, 800),
    LEVEL9(801, 900),
    LEVEL_MAX(901, Integer.MAX_VALUE);

    private Integer minPoint;
    private Integer maxPoint;

    public static Level findLevel(Integer levelPoint){
        return Arrays.stream(Level.values())
                .filter(level -> (level.minPoint <= levelPoint && level.maxPoint >= levelPoint))
                .findFirst()
                .orElse(LEVEL1);
    }

}