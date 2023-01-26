package com.jwj.community.domain.entity.member.auth.embedded;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.auth.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class MemberRolesId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

}
