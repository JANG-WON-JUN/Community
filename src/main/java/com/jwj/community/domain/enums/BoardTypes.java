package com.jwj.community.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardTypes {
    DAILY("일상 게시판"),
    DEV("개발 게시판");

    private String description;
}
