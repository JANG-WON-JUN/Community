package com.jwj.community.domain.service.board;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.repository.board.BoardRepository;
import com.jwj.community.domain.service.member.MemberCheckService;
import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.exception.BoardNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Locale.getDefault;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final BoardTypeService boardTypeService;
    private final MemberCheckService memberCheckService;
    private final MessageSource messageSource;

    public Long createBoard(Board board, String email){
        board.setBoardType(boardTypeService.findByBoardType(board.getBoardType().getBoardType()));
        memberService.findByEmail(email).addBoard(board);

        return boardRepository.save(board).getId();
    }

    public Page<Board> getBoards(BoardSearchCondition condition){
        return boardRepository.getBoards(condition);
    }

    public Board getBoard(Long id){
        return findById(id);
    }

    public Long editBoard(Board board, String email){
        Board savedBoard = findById(board.getId());
        isBoardEditable(savedBoard, email);
        savedBoard.edit(board);

        return savedBoard.getId();
    }

    public void deleteBoard(Long id, String email){
        isBoardEditable(findById(id), email);
        boardRepository.deleteById(id);
    }

    public void increaseViews(Board board){
        board.increaseViews();
    }

    private Board findById(Long id){
        return boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFound(messageSource.getMessage("error.noBoard", null, getDefault())));
    }

    private boolean isBoardEditable(Board board, String email){
        return memberCheckService.isBoardEditable(board, memberService.findByEmail(email));
    }
}
