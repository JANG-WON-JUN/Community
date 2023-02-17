package com.jwj.community.web.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.jwj.community.domain.enums.Sex.MALE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
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

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_NICKNAME = "nickname";
    private final String TEST_NAME = "admin";
    private final String TEST_PASSWORD = "password";

    @Test
    @DisplayName("회원가입하기 - 모든정보 입력 시 성공")
    @Rollback(false)
    void joinWithAllInfoTest() throws Exception {
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(2023)
                .month(1)
                .day(28)
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

        mockMvc.perform(post("/api/join")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(memberCreate)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}