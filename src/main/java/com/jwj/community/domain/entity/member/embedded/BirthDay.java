package com.jwj.community.domain.entity.member.embedded;

import jakarta.persistence.Column;

public class BirthDay {

    @Column(name = "birth_year", columnDefinition = "smallint")
    private Integer year;

    @Column(name = "birth_month", columnDefinition = "smallint")
    private Integer month;

    @Column(name = "birth_day", columnDefinition = "smallint")
    private Integer day;

}
