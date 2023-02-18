package com.jwj.community.domain.repository.board;

import com.jwj.community.domain.entity.board.BoardType;
import com.jwj.community.domain.enums.BoardTypes;

public interface BoardTypeQueryRepository {

    BoardType findByBoardType(BoardTypes boardType);
}
