package com.jwj.community.config.security.utils.code;

import com.jwj.community.config.security.utils.JwtTokenUtil;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.auth.MemberRoles;
import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.web.dto.member.jwt.JwtToken;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.jwj.community.domain.enums.Roles.*;
import static com.jwj.community.web.dto.member.jwt.JwtConst.TOKEN_HEADER_PREFIX;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.io.Encoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class JwtTokenFactory {

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final String TEST_EMAIL = "admin@google.com";
    private final String TEST_PASSWORD = "1234";

    public JwtToken getRequestJwtToken(){
        Member member = MemberCreate.builder()
                .email(TEST_EMAIL)
                .build()
                .toEntity();

        //member.addRole(Roles.ROLE_ADMIN);
        //member.addRole(ROLE_MEMBER);

        JwtToken jwtToken = jwtTokenUtil.generateToken(member);
        jwtToken.setAccessToken(TOKEN_HEADER_PREFIX + " " + jwtToken.getAccessToken());

        return jwtToken;
    }

    public JwtToken getExpiredRequestJwtToken(){
        JwtToken jwtToken = getExpiredJwtToken();
        jwtToken.setAccessToken(TOKEN_HEADER_PREFIX + " " + jwtToken.getAccessToken());

        return jwtToken;
    }

    public JwtToken getJwtToken(){
        Member member = MemberCreate.builder()
                .email(TEST_EMAIL)
                .name("어드민")
                .nickname("어드민 닉네임")
                .password(password())
                .birthDay(birthDay())
                .build()
                .toEntity();

        addMemberRole(member);
        addAdminRole(member);

        return jwtTokenUtil.generateToken(member);
    }

    public JwtToken getNoEmailJwtToken(){
        Member member = MemberCreate.builder()
                .name("어드민")
                .nickname("어드민 닉네임")
                .password(password())
                .birthDay(birthDay())
                .build()
                .toEntity();

        return jwtTokenUtil.generateToken(member);
    }

    public JwtToken getNoAuthJwtToken(){
        Member member = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname("어드민 닉네임")
                .password(password())
                .birthDay(birthDay())
                .build()
                .toEntity();

        return jwtTokenUtil.generateToken(member);
    }

    public JwtToken getExpiredJwtToken(){
        Member member = MemberCreate.builder()
                .email(TEST_EMAIL)
                .nickname("어드민 닉네임")
                .password(password())
                .birthDay(birthDay())
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

    public JwtToken getNoMemberJwtToken(){
        return jwtTokenUtil.generateToken(null);
    }

    private String encodedSecretKey(String secretKey){
        return BASE64.encode(SECRET_KEY.getBytes());
    }

    private BirthDayCreate birthDay(){
        return BirthDayCreate.builder()
                .year(2023)
                .month(1)
                .day(28)
                .build();
    }

    private PasswordCreate password(){
        return PasswordCreate.builder()
                .password(TEST_PASSWORD)
                .build();
    }

    private void addAnonymousRole(Member member){
        addRole(member, ROLE_ANONYMOUS);
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
