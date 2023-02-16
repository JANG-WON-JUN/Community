package com.jwj.community.domain.service.member.auth;

import com.jwj.community.domain.entity.member.auth.RoleResources;
import com.jwj.community.domain.enums.Roles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jwj.community.domain.enums.Roles.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test") // test 시에는 test profile을 사용할 수 있도록 설정
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class RoleResourcesServiceTest {

    @Autowired
    private RoleResourcesService roleResourcesService;

    @Test
    @DisplayName("권한으로 자원 조회하기 - 익명사용자")
    void findByIdRoleRoleNameWithAnonymousTest() {
        // given
        Roles role = ROLE_ANONYMOUS;

        // when
        List<RoleResources> roleResources = roleResourcesService.findByRoleName(role);

        // then
        assertThat(roleResources.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("권한으로 자원 조회하기 - 회원")
    void findByIdRoleRoleNameWithMemberTest() {
        // given
        Roles role = ROLE_MEMBER;

        // when
        List<RoleResources> roleResources = roleResourcesService.findByRoleName(role);

        // then
        assertThat(roleResources.size()).isEqualTo(1);
        assertThat(roleResources.get(0).getId().getRole().getRoleName().getRoleName()).isEqualTo(role.getRoleName());
    }

    @Test
    @DisplayName("권한으로 자원 조회하기 - 관리자")
    void findByIdRoleRoleNameWithAdminTest() {
        // given
        Roles role = ROLE_ADMIN;

        // when
        List<RoleResources> roleResources = roleResourcesService.findByRoleName(role);

        // then
        assertThat(roleResources.size()).isEqualTo(1);
        assertThat(roleResources.get(0).getId().getRole().getRoleName().getRoleName()).isEqualTo(role.getRoleName());
    }

}