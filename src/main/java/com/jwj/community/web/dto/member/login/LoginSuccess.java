package com.jwj.community.web.dto.member.login;

import com.jwj.community.web.dto.member.jwt.JwtToken;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginSuccess {

    private JwtToken token;
    private String email;
    private String name;
    private String nickname;
    private boolean requiredPasswordChange;

    @Builder
    public LoginSuccess(JwtToken token, String email, String name,
                        String nickname, boolean requiredPasswordChange) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.requiredPasswordChange = requiredPasswordChange;
    }
}
