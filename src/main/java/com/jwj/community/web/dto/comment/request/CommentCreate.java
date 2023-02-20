package com.jwj.community.web.dto.comment.request;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
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

    private Long parentId;

    @NotNull(message = "{field.required.board}")
    private Long boardId;

    @Builder
    public CommentCreate(String comment, Long parentId, Long boardId) {
        this.comment = comment;
        this.parentId = parentId;
        this.boardId = boardId;
    }

    public Comment toEntity(){
        return Comment.builder()
                .comment(comment)
                .parent(toParentComment())
                .board(toBoard())
                .build();
    }

    private Comment toParentComment(){
        return parentId == null ? null : Comment.builder().id(parentId).build();
    }

    private Board toBoard(){
        return Board.builder().id(boardId).build();
    }
}
