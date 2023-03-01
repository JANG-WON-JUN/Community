package com.jwj.community.web.controller.member.auth;

import com.jwj.community.config.security.utils.code.JwtTokenFactory;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.domain.service.member.auth.MemberRolesService;
import com.jwj.community.domain.service.member.auth.RoleService;
import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.dto.member.request.MemberCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import static com.jwj.community.domain.enums.Roles.ROLE_ADMIN;
import static com.jwj.community.domain.enums.Sex.MALE;
import static com.jwj.community.web.common.consts.JwtConst.AUTHORIZATION;
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
    MemberService memberService;

    @Autowired
    RoleService roleService;

    @Autowired
    MemberRolesService memberRolesService;

    @Autowired
    MessageSource messageSource;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_NICKNAME = "어드민 닉네임";
    private final JwtTokenFactory jwtTokenFactory = new JwtTokenFactory();

    @BeforeEach
    public void setup(){
        // 회원 등록
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .nickname(TEST_NICKNAME)
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());
    }

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
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.noJwtToken", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_ANONYMOUS 권한으로 관리자가 접근가능한 URL에 대해 인가 불가능")
    void unAuthorizationToAdminWithAnonymousTest() throws Exception{
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.noJwtToken", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_MEMBER 권한으로 인가")
    void memberAuthorizationTest() throws Exception{
        mockMvc.perform(get("/api/member")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken(TEST_EMAIL).getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_MEMBER 권한으로 관리자가 접근가능한 URL에 대해 인가 불가능")
    void unAuthorizationToAdminWithMemberTest() throws Exception{
        mockMvc.perform(get("/api/admin")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken(TEST_EMAIL).getAccessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errorCode").value("403"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("auth.forbidden", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("ROLE_ADMIN 권한으로 인가")
    void adminAuthorizationTest() throws Exception{
        Member savedMember = memberService.findByEmail(TEST_EMAIL);

        addRole(savedMember, ROLE_ADMIN);

        mockMvc.perform(get("/api/admin")
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken(TEST_EMAIL).getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private void addRole(Member member, Roles roles) {
        Role savedRole = roleService.findByRoleName(roles);
        member.addRole(memberRolesService.createMemberRoles(member, savedRole));
    }
}
