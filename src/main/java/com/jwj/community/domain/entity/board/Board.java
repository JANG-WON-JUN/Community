package com.jwj.community.domain.entity.board;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.jwj.community.domain.enums.State.ACTIVE;
import static com.jwj.community.domain.enums.State.DISABLE;
import static java.lang.Integer.MAX_VALUE;

@Entity
@Table(name = "BOARD_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int views;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(7)")
    private State state = ACTIVE;

    private boolean tempSave;

    @OneToOne
    @Setter
    private BoardType boardType;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Member member;

    @Builder
    public Board(Long id, String title, String content, boolean tempSave, BoardType boardType) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tempSave = tempSave;
        this.boardType = boardType;
    }

    public void setWriter(Member member){
        this.member = member;
    }

    public void edit(Board board){
        this.title = board.getTitle();
        this.content = board.getContent();
        this.tempSave = board.isTempSave();
    }

    public int increaseViews(){
        return views + 1 < MAX_VALUE ? ++views : MAX_VALUE;
    }

    public void deActivate(){
        state = DISABLE;
    }

    public void activate(){
        state = ACTIVE;
    }

    public void addComment(Comment comment){
        if(comments.size() > 0) {
            comments.remove(comment);
        }
        comments.add(comment);
    }
}
