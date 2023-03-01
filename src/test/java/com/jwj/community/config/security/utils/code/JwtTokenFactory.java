package com.jwj.community.config.security.utils.code;

import com.jwj.community.config.security.utils.JwtTokenUtil;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.auth.MemberRoles;
import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.web.dto.member.jwt.JwtToken;
import com.jwj.community.web.dto.member.request.MemberCreate;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import static com.jwj.community.domain.enums.Roles.ROLE_ADMIN;
import static com.jwj.community.domain.enums.Roles.ROLE_MEMBER;
import static com.jwj.community.web.common.consts.JwtConst.TOKEN_HEADER_PREFIX;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.io.Encoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JwtTokenFactory {
    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_PASSWORD = "1234";
    private final String SECRET_KEY = "47edd4a2-8555-4078-9b53-b652e11fc5dd";
    private final JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(SECRET_KEY);

    public JwtToken getRequestJwtToken(String email){
        Member member = MemberCreate.builder()
                .email(email)
                .build()
                .toEntity();

        JwtToken jwtToken = jwtTokenUtil.generateToken(member);
        jwtToken.setAccessToken(TOKEN_HEADER_PREFIX + " " + jwtToken.getAccessToken());

        return jwtToken;
    }

    public JwtToken getRequestJwtToken(){
        return getRequestJwtToken(TEST_EMAIL);
    }

    public JwtToken getExpiredRequestJwtToken(){
        JwtToken jwtToken = getExpiredJwtToken();
        jwtToken.setAccessToken(TOKEN_HEADER_PREFIX + " " + jwtToken.getAccessToken());

        return jwtToken;
    }

    public JwtToken getJwtToken(String email){
        Member member = MemberCreate.builder()
                .email(email)
                .name("어드민")
                .nickname("어드민 닉네임")
                .password(TEST_PASSWORD)
                .year(2023)
                .month(1)
                .day(28)
                .build()
                .toEntity();

        addMemberRole(member);
        addAdminRole(member);

        return jwtTokenUtil.generateToken(member);
    }

    public JwtToken getJwtToken(){
        return getJwtToken(TEST_EMAIL);
    }

    public JwtToken getNoEmailJwtToken(){
        Member member = MemberCreate.builder()
                .name("어드민")
                .nickname("어드민 닉네임")
                .password(TEST_PASSWORD)
                .year(2023)
                .month(1)
                .day(28)
                .build()
                .toEntity();

        return jwtTokenUtil.generateToken(member);
    }

    public JwtToken getNoAuthJwtToken(String email){
        Member member = MemberCreate.builder()
                .email(email)
                .nickname("어드민 닉네임")
                .password(TEST_PASSWORD)
                .year(2023)
                .month(1)
                .day(28)
                .build()
                .toEntity();

        return jwtTokenUtil.generateToken(member);
    }
    public JwtToken getNoAuthJwtToken(){
        return getNoAuthJwtToken(TEST_EMAIL);
    }

    public JwtToken getExpiredJwtToken(String email){
        Member member = MemberCreate.builder()
                .email(email)
                .nickname("어드민 닉네임")
                .password(TEST_PASSWORD)
                .year(2023)
                .month(1)
                .day(28)
                .build()
                .toEntity();

        String expiredAccessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuedAt(new Date(0))
                .setExpiration(new Date(0))
                .signWith(hmacShaKeyFor(encodedSecretKey(SECRET_KEY).getBytes(UTF_8)), HS256)
                .compact();

        String expiredRefreshToken = Jwts.builder()
                .setSubject(member.getEmail())
                .setExpiration(new Date(0))
                .signWith(hmacShaKeyFor(encodedSecretKey(SECRET_KEY).getBytes(UTF_8)), HS256)
                .compact();

        return JwtToken.builder()
                .accessToken(expiredAccessToken)
                .refreshToken(expiredRefreshToken)
                .build();
    }
    public JwtToken getExpiredJwtToken(){
        return getExpiredJwtToken(TEST_EMAIL);
    }

    private String encodedSecretKey(String secretKey){
        return BASE64.encode(SECRET_KEY.getBytes());
    }

    private void addMemberRole(Member member){
        addRole(member, ROLE_MEMBER);
    }

    private void addAdminRole(Member member){
        addRole(member, ROLE_ADMIN);
    }

    private void addRole(Member member, Roles roles){
        Role role = Role.builder()
                .roleName(roles)
                .build();

        MemberRoles memberRole = MemberRoles.builder()
                .member(member)
                .role(role)
                .build();

        member.addRole(memberRole);
    }
}
