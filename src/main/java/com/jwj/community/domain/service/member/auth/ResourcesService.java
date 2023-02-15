package com.jwj.community.domain.service.member.auth;

import com.jwj.community.domain.entity.member.auth.Resources;
import com.jwj.community.domain.repository.member.auth.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourcesService {

    private final ResourcesRepository resourcesRepository;

    public void createResources(Resources resources){
        resourcesRepository.save(resources);
    }
}
