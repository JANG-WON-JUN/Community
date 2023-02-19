package com.jwj.community.web.controller.member.auth;

import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.annotation.WithTestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import static com.jwj.community.domain.enums.Roles.*;
import static java.util.Locale.getDefault;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class AuthorizationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MessageSource messageSource;

    private final String TEST_EMAIL = "admin@google.com";

    @Test
    @DisplayName("ROLE_ANONYMOUS 권한으로 인가")
    void anonymousAuthorizationTest() throws Exception{
        mockMvc.perform(get("/api/anonymous"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_ANONYMOUS 권한으로 회원이 접근가능한 URL에 대해 인가 불가능")
    void unAuthorizationToMemberWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/member"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("auth.unauthorized", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_ANONYMOUS 권한으로 관리자가 접근가능한 URL에 대해 인가 불가능")
    void unAuthorizationToAdminWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("auth.unauthorized", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_MEMBER 권한으로 인가")
    @WithTestUser(email = TEST_EMAIL, role = ROLE_MEMBER)
    void memberAuthorizationTest() throws Exception{
        mockMvc.perform(get("/api/member"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_MEMBER 권한으로 관리자가 접근가능한 URL에 대해 인가 불가능")
    @WithTestUser(email = TEST_EMAIL, role = ROLE_MEMBER)
    void unAuthorizationToAdminWithMemberTest() throws Exception{
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errorCode").value("403"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("auth.forbidden", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_ADMIN 권한으로 인가")
    @WithTestUser(email = TEST_EMAIL, role = ROLE_ADMIN)
    void adminAuthorizationTest() throws Exception{
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
