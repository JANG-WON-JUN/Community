package com.jwj.community.domain.entity.member.auth.embedded;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.auth.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

// 엔티티에 포함되는 embedded 객체도 기본생성자 필요
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRolesId {

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @Builder
    public MemberRolesId(Member member, Role role){
        this.member = member;
        this.role = role;
    }
}
