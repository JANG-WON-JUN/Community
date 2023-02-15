package com.jwj.community.domain.repository.member.auth;

import com.jwj.community.domain.entity.member.auth.RoleResources;
import com.jwj.community.domain.entity.member.auth.embedded.RoleResourcesId;
import com.jwj.community.domain.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleResourcesRepository extends JpaRepository<RoleResources, RoleResourcesId> {

    // QueryDsl에서 EmbeddedId를 지원하지 않으므로 Query Methods로 처리
    List<RoleResources> findByIdRoleRoleName(Roles roleName);

}
