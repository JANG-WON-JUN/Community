package com.jwj.community.web.dto.board.response;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.enums.State;
import com.jwj.community.web.dto.member.response.BoardWriter;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardResponse {

    private String title;
    private String content;
    private Integer views;
    private State state;
    private boolean tempSave;
    private BoardTypeResponse boardType;
    private BoardWriter writer;

    @Builder
    public BoardResponse(Board board) {

        BoardTypeResponse boardTypeResponse = BoardTypeResponse.builder()
                .boardTypes(board.getBoardType().getBoardType())
                .build();

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
        this.boardType = boardTypeResponse;
        this.writer = boardWriter;
    }
}
