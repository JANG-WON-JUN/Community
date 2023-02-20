package com.jwj.community.web.dto.board.request;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.BoardType;
import com.jwj.community.domain.enums.BoardTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardCreate {

    @NotBlank(message = "{field.required.title}")
    private String title;

    private String content;

    private boolean tempSave;

    @NotNull(message = "{field.required.boardType}")
    private BoardTypes boardType;

    @Builder
    public BoardCreate(String title, String content, boolean tempSave, BoardTypes boardType) {
        this.title = title;
        this.content = content;
        this.tempSave = tempSave;
        this.boardType = boardType;
    }

    public Board toEntity(){
        return Board.builder()
                .title(title)
                .content(content)
                .tempSave(tempSave)
                .boardType(toBoardType())
                .build();
    }

    private BoardType toBoardType(){
        return BoardType.builder()
                .boardType(boardType)
                .build();
    }
}
