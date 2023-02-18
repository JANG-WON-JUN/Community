package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.web.annotation.ServiceTest;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jwj.community.domain.enums.Sex.MALE;
import static com.jwj.community.utils.CommonUtils.relativeMinuteFromNow;
import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
class PasswordServiceTest {

    @Autowired
    MemberService memberService;

    private Password password;

    private final String TEST_EMAIL = "admin@google.com";

    @BeforeEach
    public void setup() {
        // 회원 등록
        BirthDayCreate birthDay = BirthDayCreate.builder()
                .year(2023)
                .month(1)
                .day(28)
                .build();

        PasswordCreate passwordCreate = PasswordCreate.builder()
                .password("1234")
                .build();

        MemberCreate memberCreate = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .password(passwordCreate)
                .birthDay(birthDay)
                .sex(MALE)
                .build();

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPassword().toEntity());
        password = memberService.findByEmail(TEST_EMAIL).getPassword();
    }

    @Test
    @DisplayName("비밀번호 실패횟수 증가")
    void addFailCount() {
        // given
        Integer loginFailCount = loginFailSuccessively(1);

        // expected
        assertThat(loginFailCount).isEqualTo(1);
        assertThat(password.isLoginLocked()).isFalse();
    }

    @Test
    @DisplayName("비밀번호 5번 실패 시 로그인 제한")
    void loginLockTest() {
        // given
        Integer loginFailCount = loginFailSuccessively(5);

        // when
        password.loginLock();

        // then
        assertThat(loginFailCount).isEqualTo(5);
        assertThat(password.isLoginLocked()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 5번 실패 시 로그인 제한 풀 수 있는지 확인")
    void isReleasableLoginLockTest() {
        // given
        Integer loginFailCount = loginFailSuccessively(5);

        // when
        password.loginLock();

        // then
        assertThat(loginFailCount).isEqualTo(5);
        assertThat(password.isReleasableLoginLock()).isFalse();
    }

    @Test
    @DisplayName("비밀번호 5번 실패 시 1분 지나서 로그인 제한 풀 수 있는지 확인")
    void isReleasableLoginLockAfterAMinuteTest() {
        // given
        Integer loginFailCount = loginFailSuccessively(5);

        // when
        password.loginLock();
        boolean isReleasable = password.isReleasableLoginLock();
        boolean isReleasableAfterAMinute = password.isReleasableLoginLock(relativeMinuteFromNow(2));
        password.releaseLoginLock();

        // then
        assertThat(loginFailCount).isEqualTo(5);
        assertThat(isReleasable).isFalse();
        assertThat(isReleasableAfterAMinute).isTrue();
        assertThat(password.isLoginLocked()).isFalse();
    }

    private Integer loginFailSuccessively(int count){
        for(int i = 0; i < count; i++){
            password.addLoginFailCount();
        }
        return password.getLoginFailCount();
    }

}