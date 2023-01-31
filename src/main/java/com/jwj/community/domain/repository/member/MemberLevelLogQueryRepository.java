package com.jwj.community.domain.repository.member;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.MemberLevelLog;

import java.util.List;

public interface MemberLevelLogQueryRepository {

    List<MemberLevelLog> findByMember(Member member);

}
