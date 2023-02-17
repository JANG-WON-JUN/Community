package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordCreate {

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    @Builder
    public PasswordCreate(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public Password toEntity(){
        return Password.builder()
                .password(password)
                .build();
    }
}
