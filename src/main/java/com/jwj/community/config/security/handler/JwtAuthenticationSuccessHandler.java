package com.jwj.community.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.utils.JwtTokenUtil;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.domain.service.member.RefreshTokenService;
import com.jwj.community.web.dto.member.jwt.JwtToken;
import com.jwj.community.web.dto.member.login.LoginSuccess;
import com.jwj.community.web.dto.member.login.RefreshTokenCreate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Member member = (Member) authentication.getPrincipal();
        JwtToken jwtToken = jwtTokenUtil.generateToken(member);
        String email = member.getEmail();
        LoginSuccess loginSuccess = LoginSuccess.builder()
                .token(jwtToken)
                .email(email)
                .name(member.getName())
                .build();

        addLevelPoint(member);
        saveRefreshToken(jwtToken, email);

        response.setStatus(OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        objectMapper.writeValue(response.getWriter(), loginSuccess);
    }

    private void addLevelPoint(Member member) {
        memberService.addMemberPoint(member);
    }

    private void saveRefreshToken(JwtToken jwtToken, String email) {
        RefreshTokenCreate refreshTokenCreate = RefreshTokenCreate.builder()
                .refreshToken(jwtToken.getRefreshToken())
                .build();
        refreshTokenService.createRefreshToken(refreshTokenCreate.toEntity(), email);
    }
}
