package com.jwj.community.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.config.LoginContext;
import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.config.security.utils.JwtTokenUtil;
import com.jwj.community.web.exception.AccessTokenNotFound;
import com.jwj.community.web.exception.dto.ErrorResult;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.jwj.community.utils.CommonUtils.isEmpty;
import static com.jwj.community.web.common.consts.AuthPathConst.REQUIRED_AUTH_PATHS;
import static com.jwj.community.web.common.consts.JwtConst.AUTHORIZATION;
import static com.jwj.community.web.common.consts.JwtConst.TOKEN_HEADER_PREFIX;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.getDefault;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String authorizationHeader = String.valueOf(request.getHeader(AUTHORIZATION));

        if (!isRequiredAuthPath(requestURI)) {
            filterChain.doFilter(request, response);
        }else if (!isValidAuthHeader(authorizationHeader)) {
            handleInvalidToken(response);
        }else if(getContext().getAuthentication() != null){
            // ?????? authentication ????????? ????????? ?????? ????????? ???????????? ????????? ????????? ?????? ??????
            filterChain.doFilter(request, response);
        }else{
            try{
                // ???????????? username??? ???????????? SecurityContextHolder??? ?????? ("bearer 6????????? ???????????? 1????????? ???????????? ?????????)
                if(!jwtTokenUtil.isExpiredToken(authorizationHeader)){
                    LoginContext loginContext = (LoginContext) userDetailsService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(authorizationHeader));
                    // authenticationToken ?????? ??? ?????????????????? ???????????? ???????????? ?????????????????? ????????? ???????????? ????????????.
                    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(loginContext.getUsername(), loginContext.getPassword(), loginContext.getAuthorities());
                    getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request, response);
                }
            }catch(ExpiredJwtException e){
                handleExpiredJwtException(response, e);
            }catch(SignatureException e){
                handleSignatureException(response, e);
            }
        }
    }

    private boolean isRequiredAuthPath(String requestURI){
        return Arrays.stream(REQUIRED_AUTH_PATHS)
                .anyMatch(e -> new AntPathMatcher().match(e, requestURI));
    }

    private boolean isValidAuthHeader(String header){
        return !isEmpty(header) && header.startsWith(TOKEN_HEADER_PREFIX);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        String errorMessage = messageSource.getMessage("error.noJwtToken", null, getDefault());
        setErrorResponse(response, errorMessage, new AccessTokenNotFound());
    }

    private void handleExpiredJwtException(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        String errorMessage = messageSource.getMessage("error.expiredJwtToken", null, getDefault());
        setErrorResponse(response, errorMessage, e);
    }

    private void handleSignatureException(HttpServletResponse response, SignatureException e) throws IOException {
        String errorMessage = messageSource.getMessage("error.invalidJwtSignature", null, getDefault());
        setErrorResponse(response, errorMessage, e);
    }

    private void setErrorResponse(HttpServletResponse response, String errorMessage, Exception ex) throws IOException {
        log.error(errorMessage);

        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(UNAUTHORIZED)
                .errorMessage(errorMessage)
                .exception(ex)
                .build();

        objectMapper.writeValue(response.getWriter(), errorResult);
    }
}
