package com.jwj.community.domain.repository.comment;

import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.board.Comment;

import java.util.List;

public interface CommentQueryRepository {

    Integer getMaxCommentGroup(Board board);

    Integer getMaxCommentOrder(Board board, Integer commentGroup);

    List<Comment> getComments(Board board);
}
