package com.jwj.community.web.dto.member.request;

import com.jwj.community.domain.entity.member.embedded.BirthDay;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// DTO 안에 DTO가 들어가고, @RequestBody로 매핑하기 위해선 기본생성자가 필요하다.
@NoArgsConstructor
public class BirthDayCreate {

    private Integer year;

    private Integer month;

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
