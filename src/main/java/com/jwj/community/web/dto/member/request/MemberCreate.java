package com.jwj.community.web.dto.member.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.domain.entity.member.embedded.BirthDay;
import com.jwj.community.domain.enums.Sex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

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

    @NotBlank(message = "{field.required.password}")
    private String password;

    @NotBlank(message = "{field.required.confirmPassword}")
    private String confirmPassword;

    @NotNull(message = "{field.required.birthYear}")
    @Min(value = 1990, message = "{field.range.birthYear}")
    private Integer year;

    @NotNull(message = "{field.required.birthMonth}")
    @Range(min = 1, max = 12, message = "{field.range.birthMonth}")
    private Integer month;

    @NotNull(message = "{field.required.birthDay}")
    @Range(min = 1, max = 31, message = "{field.range.birthDay}")
    private Integer day;

    @NotNull(message = "{field.required.sex}")
    private Sex sex;

    @Builder
    public MemberCreate(String email, String name, String nickname,
                        String password, String confirmPassword,
                        Integer year, Integer month, Integer day, Sex sex) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.year = year;
        this.month = month;
        this.day = day;
        this.sex = sex;
    }

    public Member toEntity(){
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .birthDay(toBirthDay())
                .sex(sex)
                .build();
    }

    @JsonIgnore
    // Jackson 라이브러리로 serialize 될 때 get, set 메소드가 있으면 필드로 취급한다.
    // 필드 취급하지 않는 getter 메소드, setter 메소드가 영향을 끼치지 않도록 @JsonIgnore를 선언한다.
    public Password getPasswordEntity(){
        return Password.builder()
                .password(password)
                .build();
    }

    private BirthDay toBirthDay(){
        return BirthDay.builder()
                .year(year)
                .month(month)
                .day(day)
                .build();
    }
}
