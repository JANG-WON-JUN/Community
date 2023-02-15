package com.jwj.community.domain.repository.member.auth.impl;

import com.jwj.community.domain.entity.member.auth.Resources;
import com.jwj.community.domain.enums.ResourceType;
import com.jwj.community.domain.repository.member.auth.ResourcesQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jwj.community.domain.entity.member.auth.QResources.resources;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;

@Repository
@RequiredArgsConstructor
public class ResourcesQueryRepositoryImpl implements ResourcesQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Resources> findByResourceType(ResourceType resourceType) {
        return queryFactory
                .selectFrom(resources)
                .where(resourceTypeEq(resourceType))
                .orderBy(resources.orderNum.asc())
                .fetch();
    }

    private BooleanBuilder resourceTypeEq(ResourceType resourceType) {
        return nullSafeBuilder(() -> resources.resourceType.eq(resourceType));
    }

}
