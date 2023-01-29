package com.jwj.community.web.dto.member.login;

import com.jwj.community.web.dto.member.jwt.JwtToken;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginSuccess {

    private JwtToken token;
    private String email;
    private String name;

    @Builder
    public LoginSuccess(JwtToken token, String email, String name) {
        this.token = token;
        this.email = email;
        this.name = name;
    }
}
