package com.jwj.community.domain.repository.comment;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;
import com.jwj.community.web.common.paging.request.CommentSearchCondition;
import org.springframework.data.domain.Page;

public interface CommentQueryRepository {

    Integer getMaxCommentGroup(Board board);

    Integer getMaxCommentOrder(Board board, Integer commentGroup);

    Page<Comment> getComments(CommentSearchCondition condition, Board board);
}
