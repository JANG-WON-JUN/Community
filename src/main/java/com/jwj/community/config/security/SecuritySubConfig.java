package com.jwj.community.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SecurityConfig 1개 클래스에서 모든 빈을 주입받을 때 순환참조가 일어나는 경우가 있어
 * 순환참조 방지용으로 SecuritySubConfig 클래스 선언
 */
@Configuration
public class SecuritySubConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
