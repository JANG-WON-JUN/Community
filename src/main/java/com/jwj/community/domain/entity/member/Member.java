package com.jwj.community.domain.entity.member;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.board.Board;
import com.jwj.community.domain.entity.member.auth.MemberRoles;
import com.jwj.community.domain.entity.member.embedded.BirthDay;
import com.jwj.community.domain.enums.Sex;
import com.jwj.community.domain.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "MEMBER_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, columnDefinition = "varchar(100)", nullable = false)
    private String email;

    @Column(columnDefinition = "varchar(100)")
    private String name;

    @Embedded
    private BirthDay birthDay;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(6)")
    private Sex sex;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(7)")
    private State state;

    @Setter
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Password password;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MemberLevelLog> memberLevelLogs = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EmailAuth> emailAuths = new ArrayList<>();

    // 만약 mappedBy할 대상이 복합키를 가지고 있다면, (복합키 필드명.복합키안의 필드명) 이렇게 설정해주면 된다.
    @OneToMany(mappedBy = "id.member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MemberRoles> memberRoles = new HashSet<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();

    @Builder
    public Member(String email, String name, BirthDay birthDay, Sex sex, State state) {
        this.email = email;
        this.name = name;
        this.birthDay = birthDay;
        this.sex = sex;
        this.state = state;
    }

    public void changeState(State state) {
        this.state = state;
    }

    public void addRole(MemberRoles memberRole) {
        memberRoles.add(memberRole);
    }
}
