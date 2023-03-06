package com.jwj.community.web.dto.comment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwj.community.domain.entity.board.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class CommentView {

    private Long id;
    private Long parentId;
    private String comment;
    private Integer commentGroup;
    private Integer commentOrder;
    private List<CommentView> replies;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    private String writerNickname;

    @Builder
    public CommentView(Comment comment) {
        this.id = comment.getId();
        this.parentId = comment.getParent() == null ? null : comment.getParent().getId();
        this.comment = comment.getComment();
        this.commentGroup = comment.getCommentGroup();
        this.commentOrder = comment.getCommentOrder();
        this.regDate = comment.getRegDate();
        this.writerNickname = comment.getMember().getNickname();
        this.replies = parseReplies(comment.getChildren());
    }

    private List<CommentView> parseReplies(List<Comment> comments){
        return comments.stream()
                .map(c -> CommentView.builder().comment(c).build())
                .collect(toList());
    }
}
