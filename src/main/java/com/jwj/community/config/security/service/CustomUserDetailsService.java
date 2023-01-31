package com.jwj.community.config.security.service;

import com.jwj.community.config.security.config.LoginContext;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

/**
 * 사용자 정보를 확인하는 UserDetailsService 클래스
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // MemberService로 변경하면 순환참조 오류 발생하므로 MemberRepository로 선언
    private final MemberRepository memberRepository;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        return new LoginContext(member,
                member.getMemberRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getId().getRole().getRoleName().name()))
                        .collect(toList()));
    }
}
