package com.jwj.community.web.annotation;

import com.jwj.community.domain.enums.Roles;
import com.jwj.community.config.security.factory.TestMockUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithSecurityContext(factory = TestMockUserSecurityContextFactory.class)
public @interface WithTestUser {
    String email();
    String password() default "1234";
    Roles role();
}
