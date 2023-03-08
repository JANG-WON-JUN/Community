package com.jwj.community.domain.entity.board;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMMENT_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private Integer commentGroup = 1; // 댓글과 대댓글을 묶는 그룹번호
    
    private Integer commentOrder = 1; // 같은 그룹 내에서 순서 (등록일자 순으로 부여)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @Setter
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    @Setter
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Member member;

    @Builder
    public Comment(Long id, String comment, Comment parent, Board board) {
        this.id = id;
        this.comment = comment;
        this.parent = parent;
        this.board = board;
    }

    public void setWriter(Member member){
        this.member = member;
    }

    public void setCommentGroup(Integer commentGroup){
        this.commentGroup = commentGroup == null ? 1 : commentGroup;
    }

    public void setCommentOrder(Integer commentOrder){
        this.commentOrder = commentOrder == null ? 1 : commentOrder;
    }

    public void edit(Comment comment) {
        this.comment = comment.getComment();
    }

    public void addChild(Comment childComment){
        if(children.size() > 0) {
            children.remove(childComment);
        }
        childComment.setParent(this);
        children.add(childComment);
    }
}
