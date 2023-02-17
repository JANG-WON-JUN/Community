package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.enums.Sex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberCreate {

    @Email(message = "{field.required.emailFormat}")
    @NotBlank(message = "{field.required.email}")
    private String email;

    @NotBlank(message = "{field.required.name}")
    private String name;

    @NotBlank(message = "{field.required.nickname}")
    private String nickname;

    @Valid // dto 내부 필드의 유효성 검사를 할 때 @Valid를 붙이면 됨
    private PasswordCreate password;

    @Valid
    private BirthDayCreate birthDay;

    @NotNull(message = "{field.required.sex}")
    private Sex sex;

    @Builder
    public MemberCreate(String email, String name, String nickname,
                        PasswordCreate password, BirthDayCreate birthDay, Sex sex) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.birthDay = birthDay;
        this.sex = sex;
    }

    public Member toEntity(){
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .birthDay(birthDay.toBirthDay())
                .sex(sex)
                .build();
    }
}
