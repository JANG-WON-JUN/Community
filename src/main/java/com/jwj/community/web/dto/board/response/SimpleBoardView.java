package com.jwj.community.web.dto.board.response;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.enums.BoardTypes;
import com.jwj.community.domain.enums.State;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleBoardView {

    private Long id;
    private String title;
    private Integer views;
    private Integer comments;
    private State state;
    private BoardTypes boardType;
    private String writerNickname;
    private LocalDateTime regDate;

    @Builder
    public SimpleBoardView(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.views = board.getViews();
        this.state = board.getState();
        this.regDate = board.getRegDate();
        this.boardType = board.getBoardType().getBoardType();
        this.writerNickname = board.getMember().getNickname();
        this.comments = board.getComments().size();
    }
}
