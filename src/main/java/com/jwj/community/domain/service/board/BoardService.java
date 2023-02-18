package com.jwj.community.domain.service.board;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.repository.board.BoardRepository;
import com.jwj.community.domain.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final BoardTypeService boardTypeService;

    public Long createBoard(Board board, String email){
        board.setBoardType(boardTypeService.findByBoardType(board.getBoardType().getBoardType()));
        memberService.findByEmail(email).addBoard(board);

        return boardRepository.save(board).getId();
    }
}
