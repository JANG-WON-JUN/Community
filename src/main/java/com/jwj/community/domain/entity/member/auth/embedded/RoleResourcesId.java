package com.jwj.community.domain.entity.member.auth.embedded;

import com.jwj.community.domain.entity.member.auth.Resources;
import com.jwj.community.domain.entity.member.auth.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 엔티티에 포함되는 embedded 객체도 기본생성자 필요
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleResourcesId {

    // 복합키에서는 엔티티의 PK 컬럼명이 id더라도 변수명 + id로 joincolumn에 엔티티클래스명 + id로 명시해줘야 한다.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resources_id")
    private Resources resources;

    @Builder
    public RoleResourcesId(Role role, Resources resources) {
        this.role = role;
        this.resources = resources;
    }
}
