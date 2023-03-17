package com.jwj.community.domain.repository.member.auth;


import com.jwj.community.domain.entity.member.auth.Resources;
import com.jwj.community.domain.enums.ResourceType;

import java.util.List;

public interface ResourcesQueryRepository {

    List<Resources> findByResourceType(ResourceType resourceType);
}
