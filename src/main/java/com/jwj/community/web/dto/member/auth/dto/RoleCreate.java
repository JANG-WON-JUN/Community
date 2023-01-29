package com.jwj.community.web.dto.member.auth.dto;

import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.enums.Roles;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleCreate {

    private Roles role;
    private String roleDesc;

    @Builder
    public RoleCreate(Roles role, String roleDesc) {
        this.role = role;
        this.roleDesc = roleDesc;
    }

    public Role toEntity(){
        return Role.builder()
                .roleName(role)
                .roleDesc(roleDesc)
                .build();
    }
}
