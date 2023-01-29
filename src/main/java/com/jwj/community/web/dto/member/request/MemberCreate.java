package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.enums.Sex;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberCreate {

    private String email;
    private String name;
    private PasswordCreate password;
    private BirthDayCreate birthDay;
    private Sex sex;

    @Builder
    public MemberCreate(String email, String name, PasswordCreate password,
                        BirthDayCreate birthDay, Sex sex) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.birthDay = birthDay;
        this.sex = sex;
    }

    public Member toEntity(){
        return Member.builder()
                .email(email)
                .name(name)
                .birthDay(birthDay.toBirthDay())
                .sex(sex)
                .build();
    }

}
