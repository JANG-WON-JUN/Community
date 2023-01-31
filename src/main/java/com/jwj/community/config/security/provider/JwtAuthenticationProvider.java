package com.jwj.community.config.security.provider;

import com.jwj.community.config.security.config.LoginContext;
import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.web.exception.LoginLockedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.util.Locale.getDefault;

/**
 * JWT 인증으로 사용자 로그인 시 회원정보, 비밀번호를 체크하는 Provider
 */
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String requestedPassword = (String) authentication.getCredentials();

        LoginContext loginContext = (LoginContext) userDetailsService.loadUserByUsername(email);
        Password password = loginContext.getMember().getPassword();

        if(!password.isPossibleLoginCheck() && password.isLoginLocked()){
            throw new LoginLockedException(messageSource.getMessage("error.loginLocked", null, getDefault()));
        }

        if(!passwordEncoder.matches(requestedPassword, password.getPassword())){
            // 비밀번호 5회 입력 실패 시 로그인 1분 정지
            if(password.addLoginFailCount() >= 5){
                password.loginLock();
            }
            throw new BadCredentialsException(messageSource.getMessage("confirm.pwd.not.match", null, getDefault()));
        }

        return new JwtAuthenticationToken(loginContext.getMember(), null, loginContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 인증 객체가 JwtAuthenticationToken과 같을 때 authenticate 메소드 호출
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
