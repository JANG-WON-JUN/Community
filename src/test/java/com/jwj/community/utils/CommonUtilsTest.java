package com.jwj.community.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.jwj.community.utils.CommonUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;

class CommonUtilsTest {

    @Test
    @DisplayName("문자열 비어있는지 테스트")
    void isEmptyTest() {
        assertThat(isEmpty(null)).isTrue();
        assertThat(isEmpty("")).isTrue();
        assertThat(isEmpty(" ")).isTrue();
        assertThat(isEmpty("null")).isTrue();
        assertThat(isEmpty("test")).isFalse();
    }
}