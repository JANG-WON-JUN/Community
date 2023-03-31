package com.jwj.community.domain.service.member;

import com.jwj.community.web.annotation.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class EmailAuthServiceTest {

    @Autowired
    private EmailAuthService emailAuthService;

    @Test
    void sendAuthEmailTest() throws Exception{
        emailAuthService.sendAuthMail();
    }
}