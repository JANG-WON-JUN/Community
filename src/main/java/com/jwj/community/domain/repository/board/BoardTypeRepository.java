package com.jwj.community.domain.repository.board;

import com.jwj.community.domain.entity.board.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTypeRepository extends JpaRepository<BoardType, Long>, BoardTypeQueryRepository {
}
