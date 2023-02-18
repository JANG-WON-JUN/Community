package com.jwj.community.domain.entity.board;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.enums.State;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BOARD_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private int views;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(7)")
    private State state;

    private boolean tempSave;

    @OneToOne
    @Setter
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Member member;

    @Builder
    public Board(String title, String content, boolean tempSave, BoardType boardType) {
        this.title = title;
        this.content = content;
        this.tempSave = tempSave;
        this.boardType = boardType;
    }

    public void setWriter(Member member){
        this.member = member;
    }
}
