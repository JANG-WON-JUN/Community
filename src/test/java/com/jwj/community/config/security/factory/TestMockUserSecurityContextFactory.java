package com.jwj.community.config.security.factory;

import com.jwj.community.config.security.token.JwtAuthenticationToken;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.web.annotation.WithTestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;

final public class TestMockUserSecurityContextFactory implements WithSecurityContextFactory<WithTestUser> {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Override
    public SecurityContext createSecurityContext(WithTestUser withTestUser) {
        String email = withTestUser.email();
        String password = withTestUser.password();
        Roles role = withTestUser.role();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));

        Authentication authentication = new JwtAuthenticationToken(email, password, authorities);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    @Autowired(required = false)
    void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
}
