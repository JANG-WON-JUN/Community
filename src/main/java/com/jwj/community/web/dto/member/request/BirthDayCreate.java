package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.embedded.BirthDay;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
// DTO 안에 DTO가 들어가고, @RequestBody로 매핑하기 위해선 기본생성자가 필요하다.
@NoArgsConstructor
public class BirthDayCreate {

    @NotBlank
    @Min(value = 1990)
    private Integer year;

    @NotBlank
    @Range(min = 1, max = 12)
    private Integer month;

    @NotBlank
    @Range(min = 1, max = 31)
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
}
