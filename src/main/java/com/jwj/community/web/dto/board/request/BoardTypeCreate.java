package com.jwj.community.web.dto.board.request;

import com.jwj.community.domain.entity.board.BoardType;
import com.jwj.community.domain.enums.BoardTypes;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardTypeCreate {

    @NotNull(message = "{field.required.boardType}")
    private BoardTypes boardType;

    @Builder
    public BoardTypeCreate(BoardTypes boardType) {
        this.boardType = boardType;
    }

    public BoardType toEntity(){
        return BoardType.builder()
                .boardType(boardType)
                .build();
    }
}
