package com.jwj.community.domain.service.member.auth;

import com.jwj.community.domain.entity.member.auth.RoleResources;
import com.jwj.community.domain.enums.Roles;
import com.jwj.community.domain.repository.member.auth.RoleResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleResourcesService {

    private final RoleResourcesRepository roleResourcesRepository;

    public void createRoleResources(RoleResources roleResources) {
        roleResourcesRepository.save(roleResources);
    }

    @Cacheable("roleResourcesCacheStore")
    public List<RoleResources> findByRoleName(Roles role){
        return roleResourcesRepository.findByIdRoleRoleName(role);
    }
}
