package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.auth.embedded.MemberRolesId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_ROLES_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRoles extends BaseEntity {

    @EmbeddedId
    private MemberRolesId id;

    @Builder
    public MemberRoles(Member member, Role role){
        id = MemberRolesId.builder()
                .member(member)
                .role(role)
                .build();
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberRoles that = (MemberRoles) o;

        return (id.getMember().getId() == that.getId().getMember().getId() &&
                id.getRole().getId() == that.getId().getRole().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.getMember().getId() + id.getRole().getId());
    }
     */
}
