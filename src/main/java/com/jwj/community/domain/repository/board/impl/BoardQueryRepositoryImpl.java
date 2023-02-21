package com.jwj.community.domain.repository.board.impl;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.repository.board.BoardQueryRepository;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.enums.BoardSearchType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jwj.community.domain.entity.board.QBoard.board;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Board> getBoards(BoardSearchCondition condition) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .where(
                        tempSaveEq(condition.getTempSave())
                        .and(searchKeyword(condition.getSearchType(), condition.getKeyword()))
                )
                .offset(condition.getPageable().getOffset())
                .limit(condition.getPageable().getPageSize())
                .orderBy(board.id.desc())
                .fetch();

        return new PageImpl<>(boards, condition.getPageable(), boards.size());
    }

    private BooleanBuilder tempSaveEq(boolean tempSave) {
        return nullSafeBuilder(() -> board.tempSave.eq(tempSave));
    }

    private BooleanBuilder searchKeyword(BoardSearchType searchType, String keyword) {
        if(!hasText(keyword)) return null;

        return switch(searchType) {
            case TITLE -> titleLike(keyword);
            case WRITER -> writerNicknameLike(keyword);
        };
    }

    private BooleanBuilder titleLike(String keyword) {
        return nullSafeBuilder(() -> board.title.likeIgnoreCase(keyword));
    }

    private BooleanBuilder writerNicknameLike(String keyword) {
        return nullSafeBuilder(() -> board.member.nickname.likeIgnoreCase(keyword));
    }
}
