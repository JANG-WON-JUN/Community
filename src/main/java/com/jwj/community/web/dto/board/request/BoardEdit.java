package com.jwj.community.web.dto.board.request;

import com.jwj.community.domain.entity.board.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardEdit {

    private Long id;

    @NotBlank(message = "{field.required.title}")
    private String title;

    private String content;

    private boolean tempSave;

    @Builder
    public BoardEdit(Long id, String title, String content, boolean tempSave) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tempSave = tempSave;
    }

    public Board toEntity(){
        return Board.builder()
                .id(id)
                .title(title)
                .content(content)
                .tempSave(tempSave)
                .build();
    }
}
