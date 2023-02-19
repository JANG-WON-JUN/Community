package com.jwj.community.domain.repository.board;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import org.springframework.data.domain.Page;

public interface BoardQueryRepository {

    Page<Board> getBoards(BoardSearchCondition condition);

}
