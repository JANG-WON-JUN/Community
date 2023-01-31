package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.MemberLevelLog;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jwj.community.domain.enums.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // test 시에는 test profile을 사용할 수 있도록 설정
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class MemberLevelLogServiceTest {

    @Autowired
    MemberLevelLogService memberLevelLogService;

    @Autowired
    MemberService memberService;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_PASSWORD = "1234";

    @BeforeEach
    public void setup(){
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
    @DisplayName("회원 가입 시 회원 레벨 로그 남기기")
    void createLevelLogWithCreateMemberTest() {
        // given (회원가입은 @BeforeEach에서 수행)

        // when
        List<MemberLevelLog> logs = memberLevelLogService.findByEmail(TEST_EMAIL);

        // then
        assertThat(logs.size()).isEqualTo(1);
        assertThat(logs.get(0).getId()).isEqualTo(1);
    }
}