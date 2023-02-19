package com.jwj.community.web.dto.board.response;

import com.jwj.community.domain.enums.BoardTypes;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardTypeResponse {

    private BoardTypes boardTypes;

    @Builder
    public BoardTypeResponse(BoardTypes boardTypes) {
        this.boardTypes = boardTypes;
    }
}
