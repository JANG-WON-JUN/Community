package com.jwj.community.web.controller.member.auth.code;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {

    @GetMapping("/api/anonymous")
    public void anonymous(){
    }

    @GetMapping("/api/member")
    public void member(){
    }

    @GetMapping("/api/admin")
    public void admin(){
    }
}
