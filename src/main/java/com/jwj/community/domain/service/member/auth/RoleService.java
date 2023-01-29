package com.jwj.community.domain.service.member.auth;

import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.repository.member.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void createRole(Role role){
        roleRepository.save(role);
    }
}
