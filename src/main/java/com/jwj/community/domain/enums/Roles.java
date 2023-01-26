package com.jwj.community.domain.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Roles {
    // Spring Security에서는 권한을 나타내는 문자열의 접두어에 기본으로 ROLE_를 붙여주므로
    // 동일하게 Roles enum을 작성함
    ROLE_USER("비회원"),
    ROLE_MEMBER("회원"),
    ROLE_ADMIN("관리자");

    private String roleName;

    public static Roles findRole(String roleName){
        return Arrays.stream(Roles.values())
                .filter(role -> role.name().equals(roleName))
                .findFirst()
                .orElse(ROLE_USER);
    }
}
