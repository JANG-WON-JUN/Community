package com.jwj.community.utils;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.jwj.community.utils.CommonUtils.findCookieByName;
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

    @Test
    @DisplayName("쿠키 찾기 테스트")
    void findCookieByNameTest() {
        Cookie[] cookies = {
                new Cookie("name1", ""),
                new Cookie("name2", "")
        };

        assertThat(findCookieByName(null, null)).isNull();
        assertThat(findCookieByName(cookies, null)).isNull();
        assertThat(findCookieByName(cookies, "없는 name")).isNull();
        assertThat(findCookieByName(cookies, "name1").getName()).isEqualTo("name1");
    }
}