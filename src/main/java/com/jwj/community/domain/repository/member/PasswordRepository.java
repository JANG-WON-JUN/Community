package com.jwj.community.domain.repository.member;

import com.jwj.community.domain.entity.member.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}
