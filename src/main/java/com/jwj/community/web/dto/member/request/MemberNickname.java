package com.jwj.community.web.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberNickname {

    @NotBlank(message = "{field.required.nickname}")
    private String nickname;
}
