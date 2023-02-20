package com.jwj.community.web.dto.comment.request;

import com.jwj.community.domain.entity.board.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class parentComment {

    private Long parentId;

    @Builder
    public parentComment(Long parentId) {
        this.parentId = parentId;
    }

    public Comment toEntity(){
        return Comment.builder()
                .id(parentId)
                .build();
    }
}
