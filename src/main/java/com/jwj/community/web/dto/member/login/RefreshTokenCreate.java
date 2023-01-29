package com.jwj.community.web.dto.member.login;

import com.jwj.community.domain.entity.member.RefreshToken;
import lombok.Builder;
import lombok.Data;

@Data
public class RefreshTokenCreate {

    private String accessToken;
    private String refreshToken;

    @Builder
    public RefreshTokenCreate(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public RefreshToken toEntity(){
        return RefreshToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
