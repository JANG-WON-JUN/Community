package com.jwj.community.domain.entity.member.embedded;

import jakarta.persistence.Column;

public class BirthDay {

    @Column(name = "birth_year", columnDefinition = "smallint")
    private int year;

    @Column(name = "birth_month", columnDefinition = "smallint")
    private int month;

    @Column(name = "birth_day", columnDefinition = "smallint")
    private int day;

}
