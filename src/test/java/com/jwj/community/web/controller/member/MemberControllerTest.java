package com.jwj.community.web.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.jwj.community.domain.enums.Sex.MALE;
import static java.util.Locale.getDefault;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test") // test 시에는 test profile을 사용할 수 있도록 설정
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MemberService memberService;

    @Autowired
    MessageSource messageSource;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_NICKNAME = "nickname";
    private final String TEST_NAME = "admin";
    private final String TEST_PASSWORD = "password";

    @Test
    @DisplayName("회원가입하기 - 모든정보 입력 시 성공")
    void joinWithAllInfoTest() throws Exception {
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 이메일은 필수입력")
    void emailRequiredTest() throws Exception {
        MemberCreate memberCreate = MemberCreate.builder()
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("email"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.email", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 이메일 형식이 맞아야 함")
    void emailFormatTest() throws Exception {
        MemberCreate memberCreate = MemberCreate.builder()
                .email("형식에 맞치 않는 이메일!")
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("email"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.emailFormat", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 이메일은 중복되면 안됨")
    void emailDuplicateTest() throws Exception {
        createMember();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("email"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("duplicate.email", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 이름은 필수입력")
    void nameRequiredTest() throws Exception {
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name("")
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.name", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 닉네임은 필수입력")
    void nicknameRequiredTest() throws Exception {
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(null)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("nickname"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.nickname", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 닉네임은 중복되면 안됨")
    void nicknameDuplicateTest() throws Exception {
        createMember();

        MemberCreate memberCreate = MemberCreate.builder()
                .email("anotherEmail@google.com")
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("nickname"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("duplicate.nickname", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 비밀번호는 필수입력")
    void passwordRequiredTest() throws Exception {
        PasswordCreate passwordCreate = PasswordCreate.builder()
                .confirmPassword(TEST_PASSWORD)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(passwordCreate)
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("password.password"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.password", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 비밀번호 확인은 필수입력")
    void confirmPasswordRequiredTest() throws Exception {
        PasswordCreate passwordCreate = PasswordCreate.builder()
                .password(TEST_PASSWORD)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(passwordCreate)
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("password.confirmPassword"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.confirmPassword", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 비밀번호와 비밀번호 확인은 동일해야함")
    void notMatchPasswordAndConfirmPasswordTest() throws Exception {
        PasswordCreate passwordCreate = PasswordCreate.builder()
                .password("다른 비밀번호")
                .confirmPassword(TEST_PASSWORD)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(passwordCreate)
                .birthDay(validBirthDayCreate())
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("password"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("confirm.pwd.not.match", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 연도는 필수입력")
    void birthYearRequiredTest() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .month(1)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.year"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.birthYear", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 연도는 1990 이상의 숫자를 입력")
    void birthYearRangeTest1() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1990)
                .month(1)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 연도를 1990 미만의 숫자를 입력하면 안됨")
    void birthYearRangeTest2() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1899)
                .month(1)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.year"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.range.birthYear", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 월은 필수입력")
    void birthMonthRequiredTest() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.month"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.birthMonth", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 월은 1 ~ 12 사이의 숫자를 입력")
    void birthMonthRangeTest1() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1990)
                .month(1)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 월은 1미만의 숫자를 입력하면 안됨")
    void birthMonthRangeTest2() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(0)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.month"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.range.birthMonth", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 월은 12초과의 숫자를 입력하면 안됨")
    void birthMonthRangeTest3() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(13)
                .day(28)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.month"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.range.birthMonth", null, getDefault())))
                .andDo(print());
    }
    
    @Test
    @DisplayName("회원가입하기 - 생년월일의 일은 필수입력")
    void birthDayRequiredTest() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(3)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.day"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.birthDay", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 일은 1 ~ 31 사이의 숫자를 입력")
    void birthDayRangeTest1() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(3)
                .day(31)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 일은 1미만의 숫자를 입력하면 안됨")
    void birthDayRangeTest2() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(10)
                .day(0)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.day"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.range.birthDay", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 생년월일의 일은 31초과의 숫자를 입력하면 안됨")
    void birthDayRangeTest3() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(3)
                .day(32)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay.day"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.range.birthDay", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 입력받은 생년월일은 유효한 생년월일이어야 함")
    void birthDayValidTest() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(2)
                .day(31)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("birthDay"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("confirm.birthDay", null, getDefault())))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하기 - 성별은 필수입력")
    void sexRequiredTest() throws Exception {
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(validPasswordCreate())
                .birthDay(validBirthDayCreate())
                .build();

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(messageSource.getMessage("error.badRequest", null, getDefault())))
                .andExpect(jsonPath("fieldErrors[0].field").value("sex"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value(messageSource.getMessage("field.required.sex", null, getDefault())))
                .andDo(print());
    }

    private void createMember(){
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(1992)
                .month(3)
                .day(31)
                .build();

        PasswordCreate passwordCreate = PasswordCreate.builder()
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .name(TEST_NAME)
                .password(passwordCreate)
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPassword().toEntity());
    }

    private BirthDayCreate validBirthDayCreate(){
        return BirthDayCreate.builder()
                .year(1992)
                .month(3)
                .day(31)
                .build();
    }

    private PasswordCreate validPasswordCreate(){
        return PasswordCreate.builder()
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .build();
    }
}