package com.jwj.community.domain.repository.board;

import com.jwj.community.domain.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryRepository {
}
