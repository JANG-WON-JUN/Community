package com.jwj.community.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.jwj.community.domain.enums.Level.*;
import static org.assertj.core.api.Assertions.assertThat;

class LevelTest {

    @Test
    @DisplayName("레벨포인트로 레벨찾기1")
    void findLevelTest() {
        // given
        int levelPoint = 0;
        // when
        Level level = Level.findLevel(levelPoint);
        // then
        assertThat(level).isEqualTo(LEVEL1);
    }

    @Test
    @DisplayName("레벨포인트로 레벨찾기2")
    void findLevelTest2() {
        // given
        int levelPoint = Integer.MAX_VALUE;
        // when
        Level level = Level.findLevel(levelPoint);
        // then
        assertThat(level).isEqualTo(LEVEL_MAX);
    }

    @Test
    @DisplayName("레벨포인트로 레벨찾기3")
    void findLevelTest3() {
        // given
        int levelPoint = 201;
        // when
        Level level = Level.findLevel(levelPoint);
        // then
        assertThat(level).isEqualTo(LEVEL3);
    }

}