package com.jwj.community.web.controller.board;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.web.annotation.LoginMember;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.common.result.ListResult;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardEdit;
import com.jwj.community.web.dto.board.response.BoardView;
import com.jwj.community.web.dto.board.response.SimpleBoardView;
import com.jwj.community.web.dto.member.login.LoggedInMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/board")
    public ResponseEntity<ListResult<SimpleBoardView>> getBoards(@RequestBody BoardSearchCondition condition){
        Page<Board> boardPage = boardService.getBoards(condition);

        List<SimpleBoardView> boards = boardPage.stream()
                .map(board -> SimpleBoardView.builder().board(board).build())
                .collect(toList());

        ListResult<SimpleBoardView> resultList = ListResult.<SimpleBoardView>builder()
                .list(boards)
                .page(boardPage)
                .build();

        return ok(resultList);
    }

    @GetMapping("/api/board/{id}")
    public ResponseEntity<Result<BoardView>> getBoard(@PathVariable Long id){
        Board savedBoard = boardService.getBoard(id);

        Result<BoardView> result = Result.<BoardView>builder()
                .data(BoardView.builder().board(savedBoard).build())
                .build();

        return ok(result);
    }

    @PostMapping("/api/member/board")
    public ResponseEntity<Result<Long>> createBoard(@Valid @RequestBody BoardCreate boardCreate,
                                                    @LoginMember LoggedInMember loggedInMember){
        Result<Long> result = Result.<Long>builder()
                .data(boardService.createBoard(boardCreate.toEntity(), loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @PatchMapping("/api/member/board")
    public ResponseEntity<Result<Long>> editBoard(@Valid @RequestBody BoardEdit boardEdit,
                                                  @LoginMember LoggedInMember loggedInMember){
        Result<Long> result = Result.<Long>builder()
                .data(boardService.editBoard(boardEdit.toEntity(),  loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @DeleteMapping("/api/member/board/{id}")
    public void deleteBoard(@PathVariable Long id, @LoginMember LoggedInMember loggedInMember){
        boardService.deleteBoard(id, loggedInMember.getEmail());
    }
}
