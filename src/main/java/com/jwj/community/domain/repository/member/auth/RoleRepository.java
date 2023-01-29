package com.jwj.community.domain.repository.member.auth;

import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(Roles role);
}
