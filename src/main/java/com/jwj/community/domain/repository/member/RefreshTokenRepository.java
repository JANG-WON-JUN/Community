package com.jwj.community.domain.repository.member;

import com.jwj.community.domain.entity.member.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
