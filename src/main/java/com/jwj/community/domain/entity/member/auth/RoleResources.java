package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.auth.embedded.RoleResourcesId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLE_RESOURCES_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleResources extends BaseEntity {

    @EmbeddedId
    private RoleResourcesId id;

    @Builder
    public RoleResources(Role role, Resources resources){
        id = RoleResourcesId.builder()
                .role(role)
                .resources(resources)
                .build();
    }
}
