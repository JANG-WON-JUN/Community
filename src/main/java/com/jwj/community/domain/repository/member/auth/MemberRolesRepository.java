package com.jwj.community.domain.repository.member.auth;

import com.jwj.community.domain.entity.member.auth.MemberRoles;
import com.jwj.community.domain.entity.member.auth.embedded.MemberRolesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRolesRepository extends JpaRepository<MemberRoles, MemberRolesId> {
}
