package com.jwj.community.domain.repository.board.impl;

import com.jwj.community.domain.entity.board.BoardType;
import com.jwj.community.domain.enums.BoardTypes;
import com.jwj.community.domain.repository.board.BoardTypeQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.jwj.community.domain.entity.board.QBoardType.boardType1;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;

@Repository
@RequiredArgsConstructor
public class BoardTypeQueryRepositoryImpl implements BoardTypeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public BoardType findByBoardType(BoardTypes boardType) {
        return queryFactory
                .selectFrom(boardType1)
                .where(boardTypeEq(boardType))
                .fetchOne();
    }

    private BooleanBuilder boardTypeEq(BoardTypes boardType) {
        return nullSafeBuilder(() -> boardType1.boardType.eq(boardType));
    }
}
