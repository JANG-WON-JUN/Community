package com.jwj.community.domain.repository.comment.impl;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.domain.repository.comment.CommentQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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
        return queryFactory
                .select(comment1.count())
                .from(comment1)
                .where(boardEq(board))
                .fetchOne()
                .intValue();
    }

    @Override
    public Integer getMaxCommentOrder(Board board, Integer commentGroup) {
        return queryFactory
                .select(comment1.count())
                .from(comment1)
                .where(boardEq(board).and(commentGroupEq(commentGroup)))
                .fetchOne()
                .intValue();
    }

    @Override
    public List<Comment> getComments(Board board) {
        return queryFactory
                .select(comment1)
                .from(comment1)
                .where(boardEq(board))
                .orderBy(comment1.commentGroup.asc(), comment1.commentGroup.asc(), comment1.regDate.asc())
                .fetch();
    }

    private BooleanBuilder boardEq(Board board) {
        return nullSafeBuilder(() -> comment1.board.eq(board));
    }

    private BooleanBuilder commentGroupEq(Integer commentGroup) {
        return nullSafeBuilder(() -> comment1.commentGroup.eq(commentGroup));
    }


}
