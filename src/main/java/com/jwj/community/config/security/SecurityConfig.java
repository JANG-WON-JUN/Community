package com.jwj.community.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwj.community.config.security.entrypoint.JwtAuthenticationEntryPoint;
import com.jwj.community.config.security.filter.JwtAuthenticationFilter;
import com.jwj.community.config.security.filter.JwtLoginProcessingFilter;
import com.jwj.community.config.security.handler.JwtAccessDeniedHandler;
import com.jwj.community.config.security.handler.JwtAuthenticationFailureHandler;
import com.jwj.community.config.security.handler.JwtAuthenticationSuccessHandler;
import com.jwj.community.config.security.provider.JwtAuthenticationProvider;
import com.jwj.community.config.security.utils.JwtTokenUtil;
import com.jwj.community.domain.entity.member.auth.RoleResources;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.domain.service.member.RefreshTokenService;
import com.jwj.community.domain.service.member.auth.RoleResourcesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.jwj.community.domain.enums.Roles.ROLE_ANONYMOUS;
import static com.jwj.community.domain.enums.Roles.findRole;
import static com.jwj.community.web.common.consts.AuthPathConst.REQUIRED_AUTH_PATHS;
import static java.util.stream.Collectors.toList;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final ObjectMapper mapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;
    private final RoleResourcesService roleResourcesService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> access) throws Exception {
        // ?????? ???????????? access ???????????? ????????????.
        http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(REQUIRED_AUTH_PATHS).access(access)
                    .anyRequest().permitAll()
            );

        http.sessionManagement()
                .sessionCreationPolicy(STATELESS);

        http.csrf().disable();

        // Form????????? ???????????? UsernamePasswordAuthenticationFilter ????????? JWT ??????????????? ????????? ??? ????????? ??????
        http.addFilterBefore(jwtLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint(messageSource, mapper))
                .accessDeniedHandler(new JwtAccessDeniedHandler(messageSource, mapper));

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
        return new JwtAuthenticationSuccessHandler(memberService, refreshTokenService, jwtTokenUtil, mapper);
    }

    @Bean
    public AuthenticationFailureHandler jwtAuthenticationFailureHandler(){
        return new JwtAuthenticationFailureHandler(messageSource, mapper);
    }

    /*
        1. Builder ??????
            builder.add(mvcMatcherBuilder.pattern("/api/member/**"), authenticated);
            builder.add(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/board"), permitAll);
            builder.add(mvcMatcherBuilder.pattern("/customer/**"), AuthorityAuthorizationManager.hasRole("ADMIN"));

        2. Spring Security 6.0 ???????????? access voter ????????? ?????? AuthorizationFilter???  AuthorizationManager ?????? ???
            securityFilterChain??? ????????? ???????????? ??????.
     */
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> requestMatcherDelegatingAuthorizationManager(HandlerMappingIntrospector introspector) {
        // AuthorizationManager??? FunctionalInterface?????? check ???????????? ???????????? AuthorizationManager ???????????? ????????????.
        // ??? ????????? bean?????? ?????? ???, SecurityFilterChain??? ???????????? ?????? ????????? ?????? check ?????? ?????? ???
        // ??? ????????? ??????????????? ??????.(RequestMatcherDelegatingAuthorizationManager??? check ???????????? manager.check??? ????????? ??? ??? ????????? ????????????.)
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        RequestMatcher anyRequest = AnyRequestMatcher.INSTANCE;
        AuthorizationManager<RequestAuthorizationContext> authenticated = AuthenticatedAuthorizationManager.authenticated();
        AuthorizationManager<RequestAuthorizationContext> denyAll = (a, c) -> new AuthorizationDecision(false);
        AuthorizationManager<RequestAuthorizationContext> permitAll = (a, c) -> new AuthorizationDecision(true);

        return (supplier, context) -> {
            Authentication authentication = supplier.get();
            HttpServletRequest request = context.getRequest();

            RequestMatcherDelegatingAuthorizationManager.Builder builder = RequestMatcherDelegatingAuthorizationManager.builder();

            List<Roles> roles = authentication.getAuthorities().stream()
                    .map(authority -> findRole(authority.getAuthority()))
                    .collect(toList());

            // ????????????????????? ?????????????????? ????????? URI??? ????????? ????????? URI??? ?????? ??? ??????
            if(isAnonymous(roles)){
                RequestMatcherDelegatingAuthorizationManager delegate = builder
                        .add(mvcMatcherBuilder.pattern(request.getRequestURI()), authenticated)
                        .build();
                return delegate.check(() -> authentication, request);
            }

            // ????????? ??????????????????, ????????? ?????? DB?????? ????????? ??????????????? (?????? ???????????? ?????? ?????? ??? ?????????)
            for(GrantedAuthority grantedAuthority : getReachableGrantedAuthorities(authentication.getAuthorities())){
                List<RoleResources> resources = roleResourcesService.findByRoleName(Roles.findRole(grantedAuthority.toString()));

                for(RoleResources resource : resources){
                    String resourceName = resource.getId().getResources().getResourceName();
                    builder.add(mvcMatcherBuilder.pattern(resourceName), authenticated);
                }
            }
            RequestMatcherDelegatingAuthorizationManager delegate = builder.build();

            return delegate.check(() -> authentication, request);
        };
    }

    private boolean isAnonymous(List<Roles> roles){
        return roles.stream()
                .anyMatch(role -> role == ROLE_ANONYMOUS);
    }

    private Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities){
        var hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_MEMBER");
        return hierarchy.getReachableGrantedAuthorities(authorities);
    }
}
