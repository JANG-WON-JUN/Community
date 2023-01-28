package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.Password;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordCreate {

    private String password;

    @Builder
    public PasswordCreate(String password) {
        this.password = password;
    }

    public Password toEntity(){
        return Password.builder()
                .password(password)
                .build();
    }
}
