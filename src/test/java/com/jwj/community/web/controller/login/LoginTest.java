package com.jwj.community.web.controller.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.MemberLevelLog;
import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.domain.enums.Level;
import com.jwj.community.domain.service.member.MemberLevelLogService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.annotation.ControllerTest;
import com.jwj.community.web.dto.member.login.Login;
import com.jwj.community.web.dto.member.request.MemberCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.jwj.community.domain.enums.Level.LEVEL1;
import static com.jwj.community.domain.enums.Level.LEVEL2;
import static com.jwj.community.domain.enums.Sex.MALE;
import static com.jwj.community.utils.CommonUtils.relativeMinuteFromNow;
import static java.util.Locale.getDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberLevelLogService memberLevelLogService;

    private String confirmEmailMessage;
    private String confirmPasswordMessage;
    private String passwordNotMatchMessage;
    private String notExistMemberMessage;
    private String loginLockedMessage;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_PASSWORD = "1234";
    private final String USERNAME_NOT_FOUND_EX_NAME = "UsernameNotFoundException";
    private final String BAD_CREDENTIALS_EX_NAME = "BadCredentialsException";
    private final String LOGIN_LOCKED_EX_NAME = "LoginLockedException";

    @BeforeEach
    public void setup(){
        confirmEmailMessage = messageSource.getMessage("confirm.email", null, getDefault());
        confirmPasswordMessage = messageSource.getMessage("confirm.pwd", null, getDefault());
        passwordNotMatchMessage = messageSource.getMessage("confirm.pwd.not.match", null, getDefault());
        notExistMemberMessage = messageSource.getMessage("error.notExistMember", null, getDefault());
        loginLockedMessage = messageSource.getMessage("error.loginLocked", null, getDefault());

        // ?????? ??????
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("?????????")
                .nickname("????????? ?????????")
                .password(TEST_PASSWORD)
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());
    }

    @Test
    @DisplayName("?????????, ???????????? ?????? ??? ????????? ??????")
    void loginSuccessTest() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token.accessToken").isNotEmpty())
                .andExpect(jsonPath("token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("email").value(TEST_EMAIL))
                .andExpect(jsonPath("name").value("?????????"))
                .andDo(print());
    }

    @Test
    @DisplayName("?????????, ???????????? ??????????????? ???????????? ?????? ??????")
    void loginEmailFailTest() throws Exception{
        Login login = Login.builder()
                .email("?????????????????? ?????????")
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(notExistMemberMessage))
                .andDo(print());
    }

    @Test
    @DisplayName("?????????, ???????????? ??????????????? ???????????? ?????? ??????????????? ??????")
    void loginPasswordFailTest() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("???????????? ?????? ????????????")
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(passwordNotMatchMessage))
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ???????????? ??????????????????. (???????????? ?????? ?????? ???)")
    void noEmailTest() throws Exception{
        Login login = Login.builder()
                .email("")
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmEmailMessage))
                .andExpect(jsonPath("exceptionName").value(USERNAME_NOT_FOUND_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ???????????? ??????????????????. (???????????? ?????? ?????? ???)")
    void noEmailTest2() throws Exception{
        Login login = Login.builder()
                .email("   ")
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmEmailMessage))
                .andExpect(jsonPath("exceptionName").value(USERNAME_NOT_FOUND_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ???????????? ??????????????????. (???????????? null ??? ???)")
    void noEmailTest3() throws Exception{
        Login login = Login.builder()
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmEmailMessage))
                .andExpect(jsonPath("exceptionName").value(USERNAME_NOT_FOUND_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ??????????????? ??????????????????. (??????????????? ?????? ?????? ???)")
    void noPasswordTest() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("")
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmPasswordMessage))
                .andExpect(jsonPath("exceptionName").value(BAD_CREDENTIALS_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ??????????????? ??????????????????. (??????????????? ?????? ?????? ???)")
    void noPasswordTest2() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(" ")
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmPasswordMessage))
                .andExpect(jsonPath("exceptionName").value(BAD_CREDENTIALS_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ??????????????? ??????????????????. (??????????????? null ??? ???)")
    void noPasswordTest3() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmPasswordMessage))
                .andExpect(jsonPath("exceptionName").value(BAD_CREDENTIALS_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ?????????, ??????????????? ??????????????????.")
    void noEmailAndPasswordTest() throws Exception{
        Login login = Login.builder()
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(confirmEmailMessage))
                .andExpect(jsonPath("exceptionName").value(USERNAME_NOT_FOUND_EX_NAME))
            .andDo(print());
    }

    @Test
    @DisplayName("????????? ??? ???????????? ????????? ???????????? ?????? ?????? ??? ?????? ?????? ??????")
    void passwordFailCountTest() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("?????? ????????????")
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(passwordNotMatchMessage))
                .andExpect(jsonPath("exceptionName").value(BAD_CREDENTIALS_EX_NAME))
            .andDo(print());

        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        assertThat(saveMember.getPassword().getLoginFailCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("???????????? 5??? ?????? ??? ?????? ??????????????? ??????????????? ????????? ????????????")
    void loginLockTest() throws Exception{
        loginFailSuccessively(5);

        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(loginLockedMessage))
                .andExpect(jsonPath("exceptionName").value(LOGIN_LOCKED_EX_NAME))
                .andDo(print());

        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        assertThat(saveMember.getPassword().getLoginFailCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("???????????? 5??? ?????? ??? ????????? ?????? ??? 1??? ????????? ????????? ?????? ??? ??? ????????? ??????")
    void isReleasableLoginLockTest() throws Exception{
        loginFailSuccessively(5);

        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("401"))
                .andExpect(jsonPath("errorMessage").value(loginLockedMessage))
                .andExpect(jsonPath("exceptionName").value(LOGIN_LOCKED_EX_NAME))
                .andDo(print());

        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        assertThat(saveMember.getPassword().isReleasableLoginLock()).isFalse();
    }

    @Test
    @DisplayName("???????????? 5??? ?????? ??? ????????? ??????????????? ????????? ????????? ?????? ??? ??? ????????? ??????")
    void isReleasableLoginLockAfterAMinuteTest() throws Exception{
        loginFailSuccessively(5);

        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        Password password = saveMember.getPassword();

        // ????????? ????????? ????????? ????????? now??? ???????????? Lock ?????? ??? ?????? ??????.
        password.loginLock();
        boolean isReleasableAfterAMinute = password.isReleasableLoginLock(relativeMinuteFromNow(2));

        // 5??? ?????? ??? 1?????? ?????? ??????????????? ?????? ????????? ?????? ??????????????? ????????? ?????? ??? ??? ????????? ??????
        assertThat(isReleasableAfterAMinute).isTrue();
        assertThat(password.isLoginLocked()).isTrue();
    }

    @Test
    @DisplayName("???????????? 5??? ?????? ??? ????????? ?????? ??? 1??? ????????? ????????? ?????? ????????? ????????? ???????????? ??????")
    void isPossibleAfterLoginLockReleaseTest() throws Exception{
        loginFailSuccessively(5);

        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        Password password = saveMember.getPassword();

        password.loginLock();
        password.isReleasableLoginLock(relativeMinuteFromNow(2));
        password.releaseLoginLock();

        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token.accessToken").isNotEmpty())
                .andExpect(jsonPath("token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("email").value(TEST_EMAIL))
                .andExpect(jsonPath("name").value("?????????"))
                .andDo(print());

        assertThat(password.getLoginLockTime()).isNull();
        assertThat(password.getLoginFailCount()).isZero();
        assertThat(password.isLoginLocked()).isFalse();
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????????????? 1??????")
    void addLevelPointTest() throws Exception{
        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        Integer levelPointBf = saveMember.getLevelPoint();

        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token.accessToken").isNotEmpty())
                .andExpect(jsonPath("token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("email").value(TEST_EMAIL))
                .andExpect(jsonPath("name").value("?????????"))
                .andDo(print());

        Integer levelPointAf = saveMember.getLevelPoint();

        assertThat(levelPointAf - levelPointBf).isEqualTo(1);
    }

    @Test
    @DisplayName("????????? ??? ??????1?????? ??????2??? ?????????")
    void levelUpTest() throws Exception{
        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        Level levelBf = saveMember.getLevel();

        for(int i = 0; i < 100; i++){
            saveMember.addLevelPoint();
        }

        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token.accessToken").isNotEmpty())
                .andExpect(jsonPath("token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("email").value(TEST_EMAIL))
                .andExpect(jsonPath("name").value("?????????"))
                .andDo(print());

        Level levelAf = saveMember.getLevel();

        assertThat(levelBf).isEqualTo(LEVEL1);
        assertThat(levelAf).isEqualTo(LEVEL2);
    }

    @Test
    @DisplayName("????????? ??? ??????1?????? ??????2??? ????????? ??? ???????????? insert")
    void levelLogTest() throws Exception{
        Member saveMember = memberService.findByEmail(TEST_EMAIL);

        for(int i = 0; i < 100; i++){
            saveMember.addLevelPoint();
        }

        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token.accessToken").isNotEmpty())
                .andExpect(jsonPath("token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("email").value(TEST_EMAIL))
                .andExpect(jsonPath("name").value("?????????"))
                .andDo(print());

        List<MemberLevelLog> logs = memberLevelLogService.findByEmail(TEST_EMAIL);

        assertThat(logs.size()).isEqualTo(2);
        assertThat(logs.get(0).getLevel()).isEqualTo(LEVEL1);
        assertThat(logs.get(1).getLevel()).isEqualTo(LEVEL2);
    }

    @Test
    @DisplayName("????????? ??? ???????????? ??????????????? ?????? ??? ????????? false ??????")
    void passwordChangeAlertTest() throws Exception {
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token.accessToken").isNotEmpty())
                .andExpect(jsonPath("token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("email").value(TEST_EMAIL))
                .andExpect(jsonPath("name").value("?????????"))
                .andExpect(jsonPath("nickname").value("????????? ?????????"))
                .andExpect(jsonPath("requiredPasswordChange").value(false))
                .andDo(print());
    }

    private void loginFailSuccessively(int count) throws Exception {
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("?????? ????????????")
                .build();

        for(int i = 0; i < count; i++){
            mockMvc.perform(post("/api/login")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(login)));
        }
    }
}
