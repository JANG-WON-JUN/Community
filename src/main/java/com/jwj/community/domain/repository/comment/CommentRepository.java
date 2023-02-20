package com.jwj.community.domain.repository.comment;

import com.jwj.community.domain.entity.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {
}
