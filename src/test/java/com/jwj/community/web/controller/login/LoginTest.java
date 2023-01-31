package com.jwj.community.web.controller.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.domain.enums.Level;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.dto.member.login.Login;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test") // test 시에는 test profile을 사용할 수 있도록 설정
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    MemberService memberService;

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

        // 회원 등록
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(2023)
                .month(1)
                .day(28)
                .build();

        PasswordCreate passwordCreate = PasswordCreate.builder()
                .password(TEST_PASSWORD)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .password(passwordCreate)
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPassword().toEntity());
    }

    @Test
    @DisplayName("이메일, 비밀번호 입력 후 로그인 성공")
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
                .andExpect(jsonPath("name").value("어드민"))
                .andDo(print());
    }

    @Test
    @DisplayName("이메일, 비밀번호 입력했지만 존재하지 않는 회원")
    void loginEmailFailTest() throws Exception{
        Login login = Login.builder()
                .email("존재하지않는 이메일")
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
    @DisplayName("이메일, 비밀번호 입력했지만 이메일은 맞고 비밀번호가 틀림")
    void loginPasswordFailTest() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("존재하지 않는 비밀번호")
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
    @DisplayName("로그인 시 이메일은 필수입력이다. (이메일에 빈값 입력 시)")
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
    @DisplayName("로그인 시 이메일은 필수입력이다. (이메일에 공백 입력 시)")
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
    @DisplayName("로그인 시 이메일은 필수입력이다. (이메일이 null 일 때)")
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
    @DisplayName("로그인 시 비밀번호는 필수입력이다. (비밀번호에 빈값 입력 시)")
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
    @DisplayName("로그인 시 비밀번호는 필수입력이다. (비밀번호에 공백 입력 시)")
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
    @DisplayName("로그인 시 비밀번호는 필수입력이다. (비밀번호에 null 일 때)")
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
    @DisplayName("로그인 시 이메일, 비밀번호는 필수입력이다.")
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
    @DisplayName("로그인 시 이메일은 맞는데 비밀번호 잘못 입력 시 실패 횟수 증가")
    void passwordFailCountTest() throws Exception{
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("틀린 비밀번호")
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
    @DisplayName("비밀번호 5번 실패 시 맞는 비밀번호로 로그인해도 로그인 제한하기")
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
    @DisplayName("비밀번호 5번 실패 시 로그인 제한 후 1분 이내에 로그인 제한 풀 수 있는지 확인")
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
    @DisplayName("비밀번호 5번 실패 시 로그인 제한시간이 지나서 로그인 제한 풀 수 있는지 확인")
    void isReleasableLoginLockAfterAMinuteTest() throws Exception{
        loginFailSuccessively(5);

        Member saveMember = memberService.findByEmail(TEST_EMAIL);
        Password password = saveMember.getPassword();

        // 로그인 제한이 풀리는 시간을 now로 입력하고 Lock 설정 후 바로 해제.
        password.loginLock();
        boolean isReleasableAfterAMinute = password.isReleasableLoginLock(relativeMinuteFromNow(2));

        // 5번 실패 후 1분이 지난 상태에서는 아직 로그인 제한 걸려있지만 로그인 제한 풀 수 있는지 체크
        assertThat(isReleasableAfterAMinute).isTrue();
        assertThat(password.isLoginLocked()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 5번 실패 시 로그인 제한 후 1분 지나서 로그인 제한 풀리고 로그인 가능한지 확인")
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
                .andExpect(jsonPath("name").value("어드민"))
                .andDo(print());

        assertThat(password.getLoginLockTime()).isNull();
        assertThat(password.getLoginFailCount()).isZero();
        assertThat(password.isLoginLocked()).isFalse();
    }

    @Test
    @DisplayName("로그인 성공 시 레벨포인트 1증가")
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
                .andExpect(jsonPath("name").value("어드민"))
                .andDo(print());

        Integer levelPointAf = saveMember.getLevelPoint();

        assertThat(levelPointAf - levelPointBf).isEqualTo(1);
    }

    @Test
    @DisplayName("로그인 시 레벨1에서 레벨2로 레벨업")
    @Rollback(false)
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
                .andExpect(jsonPath("name").value("어드민"))
                .andDo(print());

        Level levelAf = saveMember.getLevel();

        assertThat(levelBf).isEqualTo(LEVEL1);
        assertThat(levelAf).isEqualTo(LEVEL2);
    }

    private void loginFailSuccessively(int count) throws Exception {
        Login login = Login.builder()
                .email(TEST_EMAIL)
                .password("틀린 비밀번호")
                .build();

        for(int i = 0; i < count; i++){
            mockMvc.perform(post("/api/login")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(login)));
        }
    }
}
