package com.jwj.community.domain.entity.member;

import com.jwj.community.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.jwj.community.utils.CommonUtils.relativeMinuteFromNow;
import static com.jwj.community.utils.CommonUtils.relativeMonthFromNow;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;

@Entity
@Table(name = "PASSWORD_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "char(60)")
    private String password;

    private LocalDateTime beChangedDate; // 비밀번호가 변경되어야 하는 날짜

    private Integer loginFailCount;

    private LocalDateTime loginLockTime;

    @Setter
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Member member;

    @Builder
    public Password(String password) {
        this.password = password;
    }

    @Transient
    private final Integer LOGIN_LOCK_LIMIT = 60; // 초 단위

    @Setter
    @Transient
    private LocalDateTime releaseLoginLockTime = now();

    public void extendBeChangedDate() {
        this.beChangedDate = relativeMonthFromNow(3);
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
    }

    public boolean isPossibleLoginCheck(){
        return (loginFailCount == null || loginFailCount < 5) && loginLockTime == null;
    }

    public Integer addLoginFailCount(){
        loginFailCount = loginFailCount == null ? 1 : loginFailCount + 1;
        return loginFailCount;
    }

    public void loginLock(){
        if(loginFailCount == null || loginFailCount < 5) return;

        loginLockTime = relativeMinuteFromNow(1);
    }

    public boolean isLoginLocked(){
        if(loginFailCount == null || loginLockTime == null){
            return false;
        }
        Duration duration = between(loginLockTime, releaseLoginLockTime);
        return duration.getSeconds() <= LOGIN_LOCK_LIMIT && loginFailCount >= 5;
    }

    public boolean isReleasableLoginLock(){
        if(loginLockTime == null){
            return true;
        }
        Duration duration = between(loginLockTime, releaseLoginLockTime);
        return duration.getSeconds() > LOGIN_LOCK_LIMIT && loginFailCount >= 5;
    }

    public void releaseLoginLock(){
        loginFailCount = 0;
        loginLockTime = null;
    }
}
