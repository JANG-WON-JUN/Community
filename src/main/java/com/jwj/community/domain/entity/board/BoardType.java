package com.jwj.community.domain.entity.board;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.enums.BoardTypes;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOARD_TYPE_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private BoardTypes boardType;

    @Builder
    public BoardType(BoardTypes boardType) {
        this.boardType = boardType;
    }
}
