package com.jwj.community.web.controller.member.auth;

import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.web.annotation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.jwj.community.domain.enums.Roles.ROLE_ADMIN;
import static com.jwj.community.domain.enums.Roles.ROLE_MEMBER;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class AuthorizationControllerTest {

    @Autowired
    MockMvc mockMvc;

    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    private final String TEST_EMAIL = "admin@google.com";

    @Test
    @DisplayName("글 목록 조회시에는 ROLE_ANONYMOUS 권한으로 인가가능")
    void boardListWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 목록 조회시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardListWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 목록 조회시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardListWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회시에는 ROLE_ANONYMOUS 권한으로 인가가능")
    void boardWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/board", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("글 1개 조회시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(get("/api/board", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 조회시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(get("/api/board", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 저장 시에는 ROLE_ANONYMOUS 권한으로 인가 불가능")
    void boardSaveWithAnonymousTest() throws Exception{
        mockMvc.perform(post("/api/member/board"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value(UNAUTHORIZED.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 저장 시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardSaveWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(post("/api/member/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 저장 시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardSaveWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(post("/api/member/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_ANONYMOUS 권한으로 인가 불가능")
    void boardTmpSaveWithAnonymousTest() throws Exception{
        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value(UNAUTHORIZED.value()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_MEMBER 권한으로 인가가능")
    void boardTmpSaveWithMemberTest() throws Exception{
        setRole(ROLE_MEMBER);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardTmpSaveWithAdminTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private void setRole(Roles role){
        authorities.clear();
        authorities.add(new SimpleGrantedAuthority(role.name()));

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthenticationToken(TEST_EMAIL, "1234", authorities));
    }

    @Test
    @DisplayName("글 1개 임시저장 시에는 ROLE_ADMIN 권한으로 인가가능")
    void boardTmpSaveWithAd22minTest() throws Exception{
        setRole(ROLE_ADMIN);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());

        setRole(ROLE_MEMBER);

        mockMvc.perform(post("/api/member/board/tmp"))
                .andExpect(status().isOk())
                .andDo(print());

    }
}
