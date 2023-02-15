package com.jwj.community.domain.service.member.auth;

import com.jwj.community.domain.repository.member.auth.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourcesService {

    private final ResourcesRepository resourcesRepository;

    public void createResources(){

    }
}
