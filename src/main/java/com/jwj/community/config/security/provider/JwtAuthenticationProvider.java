package com.jwj.community.config.security.provider;

import com.jwj.community.config.security.config.LoginContext;
import com.jwj.community.config.security.token.JwtAuthenticationToken;
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

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        LoginContext loginContext = (LoginContext) userDetailsService.loadUserByUsername(username);

        if(!passwordEncoder.matches(password, loginContext.getMember().getPassword().getPassword())){
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
