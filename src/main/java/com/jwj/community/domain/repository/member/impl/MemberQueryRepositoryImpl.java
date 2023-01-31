package com.jwj.community.domain.repository.member.impl;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.QMember;
import com.jwj.community.domain.repository.member.MemberQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Supplier;

import static com.jwj.community.domain.entity.member.QMember.member;
import static com.jwj.community.utils.CommonUtils.nullSafeBuilder;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    // todo 만약 시큐리티가 아닌 서비스에서 회원이 없을 떄 controllerAdvice를 탈수있다면  
    // findByEmail 두개를 합쳐도 됨
    @Override
    public Member findByEmail(String email) throws UsernameNotFoundException {
        return findByEmail(email, () -> new UsernameNotFoundException(null));
    }

    @Override
    public Member findByEmail(String email, Supplier<? extends RuntimeException> exceptionSupplier) throws RuntimeException {
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(emailEq(email))
                .fetchOne();

        return Optional.ofNullable(member).orElseThrow(exceptionSupplier);
    }

    private BooleanBuilder emailEq(String email) {
        return nullSafeBuilder(() -> member.email.eq(email));
    }

}
