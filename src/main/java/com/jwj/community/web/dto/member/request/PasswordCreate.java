package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.jwj.community.utils.CommonUtils.isEmpty;

@Data
@NoArgsConstructor
public class PasswordCreate {

    @NotBlank(message = "{field.required.password}")
    private String password;

    @NotBlank(message = "{field.required.confirmPassword}")
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

    public boolean isMatchedPasswordAndConfirmPassword(){
        if(isEmpty(password) || isEmpty(confirmPassword)){
            return false;
        }

        return password.equals(confirmPassword);
    }
}
