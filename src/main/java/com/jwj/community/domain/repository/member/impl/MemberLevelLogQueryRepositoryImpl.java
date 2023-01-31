package com.jwj.community.domain.repository.member.impl;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.MemberLevelLog;
import com.jwj.community.domain.repository.member.MemberLevelLogQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jwj.community.domain.entity.member.QMemberLevelLog.memberLevelLog;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;

@Repository
@RequiredArgsConstructor
public class MemberLevelLogQueryRepositoryImpl implements MemberLevelLogQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberLevelLog> findByMember(Member member) {
        return queryFactory
                .selectFrom(memberLevelLog)
                .where(memberEq(member))
                .orderBy(memberLevelLog.id.asc())
                .fetch();
    }

    private BooleanBuilder memberEq(Member member) {
        return nullSafeBuilder(() -> memberLevelLog.member.eq(member));
    }

}
