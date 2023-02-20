package com.jwj.community.web.dto.board.request;

import com.jwj.community.domain.entity.board.Board;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardRetrieve {

    @NotNull(message = "{field.required.board}")
    private Long boardId;

    @Builder
    public BoardRetrieve(Long boardId) {
        this.boardId = boardId;
    }

    @NotNull(message = "{field.required.noBoard}")
    public Board toEntity(){
        return Board.builder()
                .id(boardId)
                .build();
    }
}
