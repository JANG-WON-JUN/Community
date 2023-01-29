package com.jwj.community.config.security.config;

import com.jwj.community.domain.entity.member.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 사용자 로그인 시 Spring Security 필터에서 사용할 인증용 Context 클래스
 */
@Setter
@Getter
public class LoginContext extends User {

    private Member member;

    public LoginContext(Member member, Collection<? extends GrantedAuthority> authorities) {
        super(member.getEmail(), member.getPassword().getPassword(), authorities);
        this.member = member;
    }

    // Email이 같으면 같은 객체로 판단하기 위해 equals, hashCode 재정의
    // 재정의 하지 않으면 이메일이 같아도 Spring Security에서 같은 객체로 인식하지 못함
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoginContext) {
            return this.member.getEmail().equals(((LoginContext) obj).member.getEmail());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.member.getEmail().hashCode();
    }
}
