package com.jwj.community.config.security.utils;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.web.dto.member.jwt.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.jwj.community.utils.CommonUtils.isEmpty;
import static com.jwj.community.web.dto.member.jwt.JwtConst.TOKEN_HEADER_PREFIX;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.io.Encoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private static final int EXPIRE_DAY = 7;
    private static final int EXPIRE_MONTH = 3;

    // username 으로 토큰생성
    public JwtToken generateToken(Member member){
        return JwtToken.builder()
                .accessToken(doGenerateAccessToken(member))
                .refreshToken(doGenerateRefreshToken(member))
                .build();
    }

    // jwt 토큰에서 username 조회
    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * jwt 토큰에서 권한조회 검색
     * 토큰을 다시 claims로 변환하면 String 값으로 변환되기 때문에
     * 다시 Set으로 만들어주어야 한다.

    public Set<Roles> getRolesFromToken(String token){
        Claims claims = getAllClaimsFromToken(token);
        List<String> roles = claims.get("roles", List.class);
        return roles.stream().map(role -> Roles.findRole(role)).collect(toSet());
    }
     */
    // jwt 토큰에서 날짜 만료 검색
    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰 검증
    public Boolean isValidToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return(username.equals(userDetails.getUsername()) && !isExpiredToken(token));
    }

    // 토큰 만료 체크
    public Boolean isExpiredToken(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String removePrefix(String token){
        if(token.startsWith(TOKEN_HEADER_PREFIX)){
            token = token.substring(TOKEN_HEADER_PREFIX.length() + 1);
        }
        return token;
    }

    // AccessToken은 사용자 정보를 담는다.
    private String doGenerateAccessToken(Member member) {
        if(member == null || isEmpty(member.getEmail())){
            return null;
        }

        return Jwts.builder()
                .setHeader(getDefaultHeader())
                // payload로써 토큰에 담을 정보들
                .setClaims(getDefaultClaims(member))
                // 토큰 제목??
                .setSubject(member.getEmail())
                // 토큰이 발급 된 시간
                .setIssuedAt(new Date(currentTimeMillis()))
                // 토큰이 만료될 시간
                //.setExpiration(new Date(DEFAULT_EXP_TIME + AT_EXP_TIME))
                .setExpiration(getAtExpTime())
                // 서명
                .signWith(hmacShaKeyFor(encodedSecretKey(secretKey).getBytes(UTF_8)), HS256)
                .compact();
    }

    // RefreshToken은 만료시간 정보만 담는다.
    private String doGenerateRefreshToken(Member member) {
        if(member == null || isEmpty(member.getEmail())){
            return null;
        }

        return Jwts.builder()
                .setSubject(member.getEmail())
                // 토큰이 만료될 시간
                .setExpiration(getRtExpTime())
                // 서명
                .signWith(hmacShaKeyFor(encodedSecretKey(secretKey).getBytes(UTF_8)), HS256)
                .compact();
    }

    // secret 키를 가지고 토큰에서 정보 검색
    private Claims getAllClaimsFromToken(String token) {
        token = removePrefix(token);
        return Jwts.parserBuilder()
                .setSigningKey(encodedSecretKey(secretKey).getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, Object> getDefaultHeader(){
        Header header = Jwts.header();
        header.put("typ", "JWT");
        return header;
    }

    private Map<String, Object> getDefaultClaims(Member member){
        Claims claims = new DefaultClaims();

        claims.put("email", member.getEmail());
        //claims.put("roles", member.getRoleSet());

        return claims;
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private String encodedSecretKey(String key){
        return BASE64.encode(key.getBytes());
    }

    private Date getAtExpTime(){
        LocalDateTime atExpTime = LocalDateTime.now().plusDays(EXPIRE_DAY);
        return Date.from(atExpTime.atZone(systemDefault()).toInstant());
    }

    private Date getRtExpTime(){
        LocalDateTime rtExpTime = now().plusMonths(EXPIRE_MONTH);
        return Date.from(rtExpTime.atZone(systemDefault()).toInstant());
    }

}