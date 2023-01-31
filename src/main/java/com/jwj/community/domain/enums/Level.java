package com.jwj.community.domain.enums;

import com.google.common.collect.Range;
import lombok.AllArgsConstructor;

import java.util.Arrays;

import static com.google.common.collect.Range.closed;

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
                .filter(level -> levelRange(level).test(levelPoint))
                .findFirst()
                .orElse(LEVEL1);
    }

    private static Range<Integer> levelRange(Level level){
        return closed(level.minPoint, level.maxPoint);
    }
}