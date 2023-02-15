package com.jwj.community.web.dto.member.auth.dto;

import com.jwj.community.domain.entity.member.auth.Resources;
import com.jwj.community.domain.enums.HttpMethod;
import com.jwj.community.domain.enums.ResourceType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResourcesCreate {

    private String resourceName;
    private HttpMethod httpMethod;
    private Integer orderNum;
    private ResourceType resourceType;

    @Builder
    public ResourcesCreate(String resourceName, HttpMethod httpMethod,
                           Integer orderNum, ResourceType resourceType) {
        this.resourceName = resourceName;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
    }

    public Resources toEntity(){
        return Resources.builder()
                .resourceName(resourceName)
                .httpMethod(httpMethod)
                .orderNum(orderNum)
                .resourceType(resourceType)
                .build();
    }
}
