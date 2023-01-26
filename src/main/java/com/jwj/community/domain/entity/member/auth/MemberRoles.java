package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.auth.embedded.MemberRolesId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_ROLES_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRoles extends BaseEntity {

    @EmbeddedId
    private MemberRolesId id;

}
