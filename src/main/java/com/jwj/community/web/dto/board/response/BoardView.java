package com.jwj.community.web.dto.board.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.enums.BoardTypes;
import com.jwj.community.domain.enums.State;
import com.jwj.community.web.dto.comment.response.CommentView;
import com.jwj.community.web.dto.member.response.BoardWriter;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class BoardView {
    private String title;
    private String content;
    private Integer views;
    private List<CommentView> comments;
    private State state;
    private boolean tempSave;
    private BoardTypes boardType;
    private BoardWriter writer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modDate;

    @Builder
    public BoardView(Board board, List<Comment> comments) {
        BoardWriter boardWriter = BoardWriter.builder()
                .id(board.getMember().getId())
                .email(board.getMember().getEmail())
                .nickname(board.getMember().getNickname())
                .build();

        this.title = board.getTitle();
        this.content = board.getContent();
        this.views = board.getViews();
        this.state = board.getState();
        this.tempSave = board.isTempSave();
        this.regDate = board.getRegDate();
        this.modDate = board.getModDate();
        this.boardType = board.getBoardType().getBoardType();
        this.comments = parseComment(comments);
        this.writer = boardWriter;
    }

    private List<CommentView> parseComment(List<Comment> comments){
        return comments.stream()
                .map(c -> CommentView.builder().comment(c).build())
                .collect(toList());
    }
}
