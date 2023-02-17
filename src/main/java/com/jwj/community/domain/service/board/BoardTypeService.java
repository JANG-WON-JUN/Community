package com.jwj.community.domain.service.board;

import com.jwj.community.domain.entity.board.BoardType;
import com.jwj.community.domain.repository.board.BoardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardTypeService {

    private final BoardTypeRepository boardTypeRepository;

    public void createBoardType(BoardType boardType){
        boardTypeRepository.save(boardType);
    }
}
