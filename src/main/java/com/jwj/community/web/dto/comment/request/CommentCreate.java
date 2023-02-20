package com.jwj.community.web.dto.comment.request;

import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.web.dto.board.request.BoardRetrieve;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentCreate {

    @NotBlank(message = "{field.required.comment}")
    private String comment;

    private parentComment parent;

    @Valid
    @NotNull(message = "{field.required.board}")
    private BoardRetrieve board;

    @Builder
    public CommentCreate(String comment, parentComment parent, BoardRetrieve board) {
        this.comment = comment;
        this.parent = parent;
        this.board = board;
    }

    public Comment toEntity(){
        return Comment.builder()
                .comment(comment)
                .parent(parent == null ? null : parent.toEntity())
                .board(board.toEntity())
                .build();
    }
}
