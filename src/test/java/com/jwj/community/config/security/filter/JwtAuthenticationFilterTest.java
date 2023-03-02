package com.jwj.community.config.security.filter;

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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class JwtAuthenticationFilterTest {

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
    private final JwtTokenFactory jwtTokenFactory = new JwtTokenFactory();

    @BeforeEach
    public void setup(){
        // 회원 등록
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .nickname("어드민 닉네임")
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());
    }

    @Test
    @DisplayName("Jwt 존재할 때 - 인가가 필요하지 않은 자원에 접근 성공")
    public void authWithJwtTokenSuccessTest() throws Exception{
        // expected
        mockMvc.perform(get("/api/anonymous")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Jwt 존재할 때 - member 권한 인가가 필요한 자원에 접근 성공")
    public void authWithJwtTokenSuccessTest2() throws Exception{
        // expected
        mockMvc.perform(get("/api/member")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Jwt 존재할 때 - admin 권한 인가가 필요한 자원에 접근 성공")
    public void authWithJwtTokenSuccessTest3() throws Exception{
        Member savedMember = memberService.findByEmail(TEST_EMAIL);

        addRole(savedMember, ROLE_ADMIN);

        // expected
        mockMvc.perform(get("/api/admin")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, jwtTokenFactory.getRequestJwtToken().getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Jwt 토큰이 존재하지 않을 때 - 인가가 필요하지 않은 자원에 접근 성공")
    public void authWithNoJwtTokenSuccessTest() throws Exception{
        // expected
        mockMvc.perform(get("/api/anonymous")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Jwt 토큰이 존재하지 않을 때 - member 권한 인가가 필요한 자원에 접근 실패")
    public void authWithNoJwtTokenFailTest1() throws Exception{
        // expected
        mockMvc.perform(get("/api/member")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.noJwtToken", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("Jwt 토큰이 존재하지 않을 때 - admin 권한 인가가 필요한 자원에 접근 실패")
    public void authWithNoJwtTokenFailTest2() throws Exception{
        // expected
        mockMvc.perform(get("/api/admin")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.noJwtToken", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("Jwt 토큰이 만료되었을 때")
    public void expiredTokenTest() throws Exception {
        // expected
        mockMvc.perform(put("/api/member")
                .header(AUTHORIZATION, jwtTokenFactory.getExpiredRequestJwtToken().getAccessToken())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.expiredJwtToken", null, getDefault())))
                .andDo(print());
    }

    private void addRole(Member member, Roles roles) {
        Role savedRole = roleService.findByRoleName(roles);
        member.addRole(memberRolesService.createMemberRoles(member, savedRole));
    }
}