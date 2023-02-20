package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.MemberLevelLog;
import com.jwj.community.web.annotation.ServiceTest;
import com.jwj.community.web.dto.member.request.MemberCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.jwj.community.domain.enums.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
class MemberLevelLogServiceTest {

    @Autowired
    MemberLevelLogService memberLevelLogService;

    @Autowired
    MemberService memberService;

    private final String TEST_EMAIL = "admin@google.com";

    @BeforeEach
    public void setup(){
        // 회원 등록
        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .password("1234")
                .year(2023)
                .month(1)
                .day(28)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());
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