package com.jwj.community.web.dto.board.request;

import com.jwj.community.domain.entity.board.Board;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @Valid
    private BoardTypeCreate boardType;

    @Builder
    public BoardCreate(String title, String content, boolean tempSave, BoardTypeCreate boardType) {
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
                .boardType(boardType.toEntity())
                .build();
    }
}
