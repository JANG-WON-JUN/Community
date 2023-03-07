package com.jwj.community.web.controller.board;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.service.board.BoardService;
import com.jwj.community.domain.service.comment.CommentService;
import com.jwj.community.web.annotation.LoginMember;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.common.paging.request.CommentSearchCondition;
import com.jwj.community.web.common.result.ListResult;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.board.request.BoardCreate;
import com.jwj.community.web.dto.board.request.BoardEdit;
import com.jwj.community.web.dto.board.response.BoardView;
import com.jwj.community.web.dto.board.response.SimpleBoardView;
import com.jwj.community.web.dto.member.login.LoggedInMember;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jwj.community.utils.CommonUtils.findCookieByName;
import static com.jwj.community.web.common.consts.CookieNameConst.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    // @PostMapping일 때는 @RequestBody를 사용하여 http body의 json 데이터를 매핑해주고
    // @GetMapping일 때는 @RequestBody를 사용하지 않아야 에러가 안난다.
    @GetMapping("/api/board")
    public ResponseEntity<ListResult<SimpleBoardView>> getBoards(BoardSearchCondition condition) {
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
    public ResponseEntity<Result<BoardView>> getBoard(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        Board savedBoard = boardService.getBoard(id);
        Cookie boardViewCookie = findCookieByName(request.getCookies(), BOARD_VIEW);

        if (!isReadBoard(boardViewCookie, savedBoard.getId())) {
            boardService.increaseViews(savedBoard);
            Cookie addedCookie = addBoardToCookie(boardViewCookie, savedBoard.getId());
            response.addCookie(addedCookie);
        }

        CommentSearchCondition condition = CommentSearchCondition.builder()
                .boardId(id)
                .build();

        BoardView boardView = BoardView.builder()
                .board(savedBoard)
                .comments(commentService.getComments(condition).getContent())
                .build();

        Result<BoardView> result = Result.<BoardView>builder()
                .data(boardView)
                .build();

        return ok(result);
    }

    @PostMapping("/api/member/board")
    public ResponseEntity<Result<Long>> createBoard(@Valid @RequestBody BoardCreate boardCreate,
                                                    @LoginMember LoggedInMember loggedInMember) {
        Result<Long> result = Result.<Long>builder()
                .data(boardService.createBoard(boardCreate.toEntity(), loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @PatchMapping("/api/member/board")
    public ResponseEntity<Result<Long>> editBoard(@Valid @RequestBody BoardEdit boardEdit,
                                                  @LoginMember LoggedInMember loggedInMember) {
        Result<Long> result = Result.<Long>builder()
                .data(boardService.editBoard(boardEdit.toEntity(), loggedInMember.getEmail()))
                .build();

        return ok(result);
    }

    @DeleteMapping("/api/member/board/{id}")
    public void deleteBoard(@PathVariable Long id, @LoginMember LoggedInMember loggedInMember) {
        boardService.deleteBoard(id, loggedInMember.getEmail());
    }

    private boolean isReadBoard(Cookie boardViewCookie, Long id) {
        return boardViewCookie != null && boardViewCookie.getValue().contains("[" + id + "]");
    }

    private Cookie addBoardToCookie(Cookie boardViewCookie, Long id) {
        String cookieValue = "[" + id + "]";

        if(boardViewCookie == null){
            boardViewCookie = new Cookie(BOARD_VIEW, cookieValue);
            boardViewCookie.setPath(COOKIE_PATH);
            boardViewCookie.setMaxAge(A_WEEK);
        }else{
            boardViewCookie.setValue(boardViewCookie.getValue() + cookieValue);
        }

        return boardViewCookie;
    }
}
