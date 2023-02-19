package com.jwj.community.web.dto.member.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BoardWriter {

    private Long id;
    private String email;
    private String nickname;

    @Builder
    public BoardWriter(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
