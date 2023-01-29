package com.jwj.community.config.init;

import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.service.member.auth.RoleService;
import com.jwj.community.web.dto.member.auth.dto.RoleCreate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class initService {

    private final RoleService roleService;

    @PostConstruct
    public void init(){
        roleInit();
    }

    private void roleInit(){
        for(Roles role : Roles.values()){
            RoleCreate roleCreate = RoleCreate.builder()
                    .role(role)
                    .roleDesc(role.getRoleName())
                    .build();
            roleService.createRole(roleCreate.toEntity());
        }
    }
}
