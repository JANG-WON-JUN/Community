package com.jwj.community.web.dto.comment.request;

import com.jwj.community.domain.entity.board.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentEdit {

    @NotNull(message = "{field.required.commentId}")
    private Long id;

    @NotBlank(message = "{field.required.comment}")
    private String comment;

    @Builder
    public CommentEdit(Long id, String comment) {
        this.id = id;
        this.comment = comment;
    }

    public Comment toEntity(){
        return Comment.builder()
                .id(id)
                .comment(comment)
                .build();
    }
}
