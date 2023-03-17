package com.jwj.community.domain.repository.member;

import com.jwj.community.domain.entity.member.MemberLevelLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLevelLogRepository extends JpaRepository<MemberLevelLog, Long>, MemberLevelLogQueryRepository {
}
