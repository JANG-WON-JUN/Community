package com.jwj.community.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.web.dto.member.login.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static com.jwj.community.utils.CommonUtils.isEmpty;

public class JwtLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    // GenericFilterBean을 상속받았기 때문에 Bean으로 등록되는데
    // 생성자 주입을 받기 위해서 AbstractAuthenticationProcessingFilter의 생성자와 동일하게 생성.
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public JwtLoginProcessingFilter(MessageSource messageSource, ObjectMapper objectMapper){
        // 요청 url이 "api/login"과 동일하면 필터 작동
        super(new AntPathRequestMatcher("/api/login"));
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 입력받은 로그인 정보 확인
        Login login = objectMapper.readValue(request.getReader(), Login.class);

        // 클라이언트에 전달하는 에러정보 (에러코드, 에러메세지 등은 JwtAuthenticationFailureHandler에서 처리)
        if(isEmpty(login.getEmail())) throw new UsernameNotFoundException(null);
        if(isEmpty(login.getPassword())) throw new BadCredentialsException(null);

        // 인증을 요청할 때 사용되는 토큰을 생성하여 AuthenticationManager로 인증 위임
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(login.getEmail(), login.getPassword()));
    }

}
