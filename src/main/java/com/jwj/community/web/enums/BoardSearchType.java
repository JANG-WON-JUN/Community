package com.jwj.community.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BoardSearchType {
    TITLE, WRITER;

    @JsonCreator
    public static BoardSearchType parse(String searchType) {
        // 만약 클라이언트에서 넘어온 값과 ENUM의 값을 다르게 설계 후
        // jackson 라이브러리로 mapping 시키고자 할 때 @JsonCreator를 선언
        // ex) 클라이언트에서 {"boardSearchType" : "t"} 를 서버로 전달하면
        // 서버에서는 enum 클래스 안에 선언된 @JsonCreator가 붙은 메소드를 사용하여
        // 매핑을 할 수 있다.
        return switch (searchType.toUpperCase()){
            case "T" -> TITLE;
            case "W" -> WRITER;
            default -> null;
        };
    }
}
