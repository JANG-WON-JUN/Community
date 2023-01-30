package com.jwj.community.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.filter.JwtLoginProcessingFilter;
import com.jwj.community.config.security.handler.JwtAuthenticationFailureHandler;
import com.jwj.community.config.security.handler.JwtAuthenticationSuccessHandler;
import com.jwj.community.config.security.provider.JwtAuthenticationProvider;
import com.jwj.community.config.security.utils.JwtTokenUtil;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.domain.service.member.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    // todo JwtAuthenticationFilter를 구현하여 매 요청마다 JWT 토큰이 유요한지 체크 및 테스트를 작성해야 한다.
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final ObjectMapper mapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;

    private final String[] whiteList = {
            "/api/login", "/api/join"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers(whiteList).permitAll()
                    .anyRequest().authenticated()
            );

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable();

        // Form인증을 담당하는 UsernamePasswordAuthenticationFilter 이전에 JWT 인증필터가 작동할 수 있도록 설정
        http.addFilterBefore(jwtLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtLoginProcessingFilter jwtLoginProcessingFilter() {
        JwtLoginProcessingFilter jwtLoginProcessingFilter = new JwtLoginProcessingFilter(messageSource, mapper);
        jwtLoginProcessingFilter.setAuthenticationManager(authenticationManager());
        jwtLoginProcessingFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler());
        jwtLoginProcessingFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler());

        return jwtLoginProcessingFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(new JwtAuthenticationProvider(userDetailsService, passwordEncoder, messageSource));
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    public AuthenticationSuccessHandler jwtAuthenticationSuccessHandler(){
        return new JwtAuthenticationSuccessHandler(refreshTokenService, jwtTokenUtil, mapper);
    }

    @Bean
    public AuthenticationFailureHandler jwtAuthenticationFailureHandler(){
        return new JwtAuthenticationFailureHandler(messageSource, mapper);
    }
}
