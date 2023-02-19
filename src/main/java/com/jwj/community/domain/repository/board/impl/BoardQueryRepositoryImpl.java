package com.jwj.community.domain.repository.board.impl;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.repository.board.BoardQueryRepository;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jwj.community.domain.entity.board.QBoard.board;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Board> boards(BoardSearchCondition condition) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .offset(condition.getPageable().getOffset())
                .limit(condition.getPageable().getPageSize())
                .orderBy(board.id.desc())
                .fetch();

        return new PageImpl<>(boards, condition.getPageable(), boards.size());
    }
}
