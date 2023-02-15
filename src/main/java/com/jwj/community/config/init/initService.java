package com.jwj.community.config.init;

import com.jwj.community.domain.entity.member.auth.Resources;
import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.entity.member.auth.RoleResources;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.service.member.auth.ResourcesService;
import com.jwj.community.domain.service.member.auth.RoleResourcesService;
import com.jwj.community.domain.service.member.auth.RoleService;
import com.jwj.community.web.dto.member.auth.dto.ResourcesCreate;
import com.jwj.community.web.dto.member.auth.dto.RoleCreate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.jwj.community.domain.enums.ResourceType.URL;
import static com.jwj.community.domain.enums.Roles.*;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class initService {

    private final RoleService roleService;
    private final ResourcesService resourcesService;
    private final RoleResourcesService roleResourcesService;

    @PostConstruct
    public void init(){
        roleInit();
        resourcesInit();
        roleResourcesInit();
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
                        .resourceType(URL)
                        .httpMethod(null)
                        .orderNum(i + 1)
                        .build();
            resourcesService.createResources(resourcesCreate.toEntity());
        }
    }

    private void roleResourcesInit(){
        List<Resources> resources = resourcesService.findByResourceType(URL);

        for(Resources resource : resources){
            Role role = switch(resource.getResourceName()){
                case "/api/member/**" -> roleService.findByRoleName(ROLE_MEMBER);
                case "/api/admin/**" -> roleService.findByRoleName(ROLE_ADMIN);
                default -> roleService.findByRoleName(ROLE_ANONYMOUS);
            };

            RoleResources roleResources = RoleResources.builder()
                    .role(role)
                    .resources(resource)
                    .build();

            roleResourcesService.createRoleResources(roleResources);
        }
    }
}
