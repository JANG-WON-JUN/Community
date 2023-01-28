package com.jwj.community.domain.entity.member.embedded;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

// 엔티티에 포함되는 embedded 객체도 기본생성자 필요
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BirthDay {

    @Column(name = "birth_year", columnDefinition = "smallint")
    private Integer year;

    @Column(name = "birth_month", columnDefinition = "smallint")
    private Integer month;

    @Column(name = "birth_day", columnDefinition = "smallint")
    private Integer day;

    @Builder
    public BirthDay(Integer year, Integer month, Integer day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
