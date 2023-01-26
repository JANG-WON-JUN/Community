package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLE_HIERARCHY_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleHierarchy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Role child;

    @OneToOne
    private Role parent;

}
