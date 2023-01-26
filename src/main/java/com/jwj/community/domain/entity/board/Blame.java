package com.jwj.community.domain.entity.board;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.enums.PostType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetAddress;

@Entity
@Table(name = "Blame_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blame extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private PostType targetType;

    @Column(name = "blame_ip")
    private InetAddress inetAddress;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Board targetBoard;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Comment targetComment;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Member targetMember; // 추천/비추천한 게시글 혹은 댓글의 작성자

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Member blameMember; // 추천/비추천한 회원

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    // 추천/비추천이 달리는 게시글
    //추천을 한 대상이 게시글이면 targetBoard와 동일하며, 추천을 한 대상이 댓글이면 댓글이 달린 게시글을 의미
    private Board board;

}
