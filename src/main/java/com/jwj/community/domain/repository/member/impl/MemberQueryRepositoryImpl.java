package com.jwj.community.domain.repository.member.impl;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.QMember;
import com.jwj.community.domain.repository.member.MemberQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.jwj.community.domain.entity.member.QMember.member;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Member findByEmail(String email) {
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(emailEq(email))
                .fetchOne();

        return ofNullable(member).orElse(null);
    }

    @Override
    public Member findByNickname(String nickname) {
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(nicknameEq(nickname))
                .fetchOne();

        return ofNullable(member).orElse(null);
    }

    private BooleanBuilder emailEq(String email) {
        return nullSafeBuilder(() -> member.email.eq(email));
    }

    private BooleanBuilder nicknameEq(String nickname) {
        return nullSafeBuilder(() -> member.nickname.eq(nickname));
    }

}
