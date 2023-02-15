package com.jwj.community.config.init;

import com.jwj.community.domain.enums.ResourceType;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.service.member.auth.ResourcesService;
import com.jwj.community.domain.service.member.auth.RoleService;
import com.jwj.community.web.dto.member.auth.dto.ResourcesCreate;
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
    private final ResourcesService resourcesService;

    @PostConstruct
    public void init(){
        roleInit();
        resourcesInit();
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

    private void resourcesInit(){
        String[] basicResources = {"/api/**", "/api/member/**", "/api/admin/**"};

        for(int i = 0; i < basicResources.length; i++){
            ResourcesCreate resourcesCreate = ResourcesCreate.builder()
                        .resourceName(basicResources[i])
                        .resourceType(ResourceType.URL)
                        .httpMethod(null)
                        .orderNum(i + 1)
                        .build();
            resourcesService.createResources(resourcesCreate.toEntity());
        }
    }
}
