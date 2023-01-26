package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.enums.Roles;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLE_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private Roles roleName;

    private String roleDesc; // 권한 상세설명

}
