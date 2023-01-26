package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.auth.embedded.RoleResourcesId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLE_RESOURCES_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleResources extends BaseEntity {

    @EmbeddedId
    private RoleResourcesId roleResourcesId;

}
