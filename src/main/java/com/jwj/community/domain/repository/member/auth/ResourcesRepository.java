package com.jwj.community.domain.repository.member.auth;

import com.jwj.community.domain.entity.member.auth.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {
}
