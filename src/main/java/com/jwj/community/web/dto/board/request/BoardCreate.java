package com.jwj.community.web.dto.board.request;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.member.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    public Board toEntity(Member member){
        return Board.builder()
                .title(title)
                .content(content)
                .tempSave(tempSave)
                .boardType(boardType.toEntity())
                .member(member)
                .build();
    }
}
