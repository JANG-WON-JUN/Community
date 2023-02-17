package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.embedded.BirthDay;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import static com.jwj.community.utils.CommonUtils.isValidDate;

@Data
// DTO 안에 DTO가 들어가고, @RequestBody로 매핑하기 위해선 기본생성자가 필요하다.
@NoArgsConstructor
public class BirthDayCreate {

    @NotNull(message = "{field.required.birthYear}")
    @Min(value = 1990, message = "{field.range.birthYear}")
    private Integer year;

    @NotNull(message = "{field.required.birthMonth}")
    @Range(min = 1, max = 12, message = "{field.range.birthMonth}")
    private Integer month;

    @NotNull(message = "{field.required.birthDay}")
    @Range(min = 1, max = 31, message = "{field.range.birthDay}")
    private Integer day;

    @Builder
    public BirthDayCreate(Integer year, Integer month, Integer day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public BirthDay toBirthDay(){
        return BirthDay.builder()
                .year(year)
                .month(month)
                .day(day)
                .build();
    }

    public boolean isValidBirthDay(){
        return isValidDate(year, month, day);
    }
}
