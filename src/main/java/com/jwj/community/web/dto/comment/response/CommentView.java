package com.jwj.community.web.dto.comment.response;

import com.jwj.community.domain.entity.board.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentView {

    private Long id;
    private String comment;
    private Integer commentGroup;
    private Integer commentOrder;
    private LocalDateTime regDate;
    private String writerNickname;

    @Builder
    public CommentView(Comment comment) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.commentGroup = comment.getCommentGroup();
        this.commentOrder = comment.getCommentOrder();
        this.regDate = comment.getRegDate();
        this.writerNickname = comment.getMember().getNickname();
    }
}
