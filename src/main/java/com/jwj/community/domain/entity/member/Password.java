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

    private int loginFailCount;

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
    private final Integer LOGIN_LOCK_LIMIT_MINUTE = 1;

    public void extendBeChangedDate() {
        this.beChangedDate = relativeMonthFromNow(3);
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
    }

    public boolean isPossibleLoginCheck(){
        return loginFailCount < 5 && loginLockTime == null;
    }

    public Integer addLoginFailCount(){
        return ++loginFailCount;
    }

    public void loginLock(){
        if(loginFailCount < 5){
            return;
        }

        loginLockTime = relativeMinuteFromNow(LOGIN_LOCK_LIMIT_MINUTE);
    }

    public boolean isLoginLocked(){
        return loginFailCount >= 5;
    }

    public boolean isReleasableLoginLock(){
        return isReleasableLoginLock(now());
    }

    public boolean isReleasableLoginLock(LocalDateTime time){
        if(loginLockTime == null){
            return true;
        }
        Duration duration = between(loginLockTime, time);
        return duration.getSeconds() > 0 && loginFailCount >= 5;
    }

    public void releaseLoginLock(){
        loginFailCount = 0;
        loginLockTime = null;
    }
}
