package com.jwj.community.web.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberEmail {

    @Email(message = "{field.required.emailFormat}")
    @NotBlank(message = "{field.required.email}")
    private String email;
}
