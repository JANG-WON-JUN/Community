package com.jwj.community.domain.repository.member;

import com.jwj.community.domain.entity.member.Member;

public interface MemberQueryRepository {

    Member findByEmail(String email);

    Member findByNickname(String nickname);

}
