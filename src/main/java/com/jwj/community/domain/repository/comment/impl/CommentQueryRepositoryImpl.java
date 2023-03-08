package com.jwj.community.domain.repository.comment.impl;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.repository.comment.CommentQueryRepository;
import com.jwj.community.web.common.paging.request.CommentSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jwj.community.domain.entity.board.QComment.comment1;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer getMaxCommentGroup(Board board) {
        Long maxCommentGroup = queryFactory
                .select(comment1.count())
                .from(comment1)
                .where(boardEq(board))
                .fetchOne();

        return maxCommentGroup == null ? null : maxCommentGroup.intValue();
    }

    @Override
    public Integer getMaxCommentOrder(Board board, Integer commentGroup) {
        Long maxCommentOrder = queryFactory
                .select(comment1.count())
                .from(comment1)
                .where(boardEq(board).and(commentGroupEq(commentGroup)))
                .fetchOne();

        return maxCommentOrder == null ? null : maxCommentOrder.intValue();
    }

    @Override
    public Page<Comment> getComments(CommentSearchCondition condition, Board board) {
        List<Comment> comments = queryFactory
                .selectFrom(comment1)
                .where(boardEq(board).and(parentNull()))
                .orderBy(comment1.commentGroup.asc(), comment1.commentGroup.asc(), comment1.regDate.asc())
                .fetch();

        long total = queryFactory
                .selectFrom(comment1)
                .where(boardEq(board))
                .fetch()
                .size();

        return new PageImpl<>(comments, condition.getPageable(), total);
    }

    private BooleanBuilder boardEq(Board board) {
        return nullSafeBuilder(() -> comment1.board.eq(board));
    }

    private BooleanBuilder parentNull() {
        return nullSafeBuilder(comment1.parent::isNull);
    }

    private BooleanBuilder commentGroupEq(Integer commentGroup) {
        return nullSafeBuilder(() -> comment1.commentGroup.eq(commentGroup));
    }
}
