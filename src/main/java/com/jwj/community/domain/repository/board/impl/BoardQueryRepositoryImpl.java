package com.jwj.community.domain.repository.board.impl;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.enums.BoardTypes;
import com.jwj.community.domain.repository.board.BoardQueryRepository;
import com.jwj.community.web.common.paging.request.BoardSearchCondition;
import com.jwj.community.web.enums.BoardSearchType;
import com.jwj.community.web.enums.SearchOrder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
                        tempSaveEq(condition.isTempSave())
                        .and(boardTypeEq(condition.getBoardType()))
                        .and(searchKeyword(condition.getSearchType(), condition.getKeyword()))
                )
                .offset(condition.getPageable().getOffset())
                .limit(condition.getPageable().getPageSize())
                .orderBy(searchOrders(condition.getSearchOrder()))
                .fetch();

        long totals = queryFactory
                .selectFrom(board)
                .where(
                        tempSaveEq(condition.isTempSave())
                        .and(boardTypeEq(condition.getBoardType()))
                        .and(searchKeyword(condition.getSearchType(), condition.getKeyword()))
                )
                .fetch()
                .size();

        return new PageImpl<>(boards, condition.getPageable(), totals);
    }

    private BooleanBuilder tempSaveEq(boolean tempSave) {
        return nullSafeBuilder(() -> board.tempSave.eq(tempSave));
    }

    private BooleanBuilder boardTypeEq(BoardTypes boardType) {
        return nullSafeBuilder(() -> board.boardType.boardType.eq(boardType));
    }

    private BooleanBuilder titleLike(String keyword) {
        return nullSafeBuilder(() -> board.title.likeIgnoreCase(keyword));
    }

    private BooleanBuilder writerNicknameLike(String keyword) {
        return nullSafeBuilder(() -> board.member.nickname.likeIgnoreCase(keyword));
    }

    private BooleanBuilder searchKeyword(BoardSearchType searchType, String keyword) {
        if(!hasText(keyword) || searchType == null) return null;

        return switch(searchType) {
            case TITLE -> titleLike(keyword);
            case WRITER -> writerNicknameLike(keyword);
        };
    }

    private OrderSpecifier<?>[] searchOrders(SearchOrder searchOrder){
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if(searchOrder != null){
            switch(searchOrder) {
                case VIEW -> orderSpecifiers.add(board.views.desc());
                // todo 추후 LIKE 순으로 정렬하는 로직 추가해야 함
                //case LIKE -> new OrderSpecifier<>(Order.DESC, board.id);
            }
        }

        orderSpecifiers.add(board.id.desc()); // 기본 정렬값

        return orderSpecifiers.toArray(OrderSpecifier<?>[]::new);
    }
}
