package com.jwj.community.config.security.utils;

import com.jwj.community.config.security.utils.code.JwtTokenFactory;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.web.dto.member.jwt.JwtToken;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static com.jwj.community.domain.enums.Roles.ROLE_ADMIN;
import static com.jwj.community.domain.enums.Roles.ROLE_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test") // test 시에는 test profile을 사용할 수 있도록 설정
class JwtTokenUtilTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    JwtTokenFactory jwtTokenFactory = new JwtTokenFactory();

    @Test
    @DisplayName("회원정보가 유효할 때 access 토큰얻기")
    void getAccessTokenTest() {
        JwtToken token = jwtTokenFactory.getJwtToken();
        assertThat(token.getAccessToken()).isNotNull();
    }


    @Test
    @DisplayName("회원정보가 유효할 때 refresh 토큰얻기")
    void getRefreshTokenTest(){
        JwtToken token = jwtTokenFactory.getJwtToken();
        assertThat(token.getRefreshToken()).isNotNull();
    }


    @Test
    @DisplayName("이메일 정보가 없을 때 access 토큰은 null")
    void getNoEmailAccessJwtTokenTest(){
        JwtToken token = jwtTokenFactory.getNoEmailJwtToken();
        assertThat(token.getAccessToken()).isNull();
    }


    @Test
    @DisplayName("이메일 정보가 없을 때 refresh 토큰은 null")
    void getNoEmailRefreshJwtTokenTest(){
        JwtToken token = jwtTokenFactory.getNoEmailJwtToken();
        assertThat(token.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("토큰에서 username 조회")
    void getUsernameFromTokenTest(){
        JwtToken token = jwtTokenFactory.getJwtToken();
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token.getAccessToken());

        assertThat(usernameFromToken).isEqualTo("admin@google.com");
    }


    @Test
    @DisplayName("토큰에서 권한조회 조회")
    void getRolesFromTokenTest(){
        JwtToken token = jwtTokenFactory.getJwtToken();
        Set<Roles> rolesFromToken = jwtTokenUtil.getRolesFromToken(token.getAccessToken());
        assertThat(rolesFromToken)
                .hasSize(2)
                .contains(ROLE_MEMBER, ROLE_ADMIN);
    }

    @Test
    @DisplayName("토큰에서 권한이 없는데 권한 조회")
    void noRolesFromTokenTest(){
        JwtToken token = jwtTokenFactory.getNoAuthJwtToken();
        Set<Roles> rolesFromToken = jwtTokenUtil.getRolesFromToken(token.getAccessToken());
        assertThat(rolesFromToken).isEmpty();
    }

    @Test
    @DisplayName("토큰이 만료 되었을 때")
    void getExpiredJwtTokenTest(){
        assertThatThrownBy(() -> {
            JwtToken token = jwtTokenFactory.getExpiredJwtToken();
            jwtTokenUtil.isExpiredToken(token.getAccessToken());
        }).isInstanceOf(ExpiredJwtException.class);
    }
}