package com.jwj.community.web.dto.board.response;

import com.jwj.community.domain.entity.board.BoardType;
import com.jwj.community.domain.enums.BoardTypes;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardTypeResponse {

    private Long id;
    private BoardTypes boardTypes;
    private String description;

    @Builder
    public BoardTypeResponse(BoardType boardType) {
        this.id = boardType.getId();
        this.boardTypes = boardType.getBoardType();
        this.description = boardType.getBoardType().getDescription();
    }
}
