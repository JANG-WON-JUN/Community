package com.jwj.community.domain.repository.member;

import com.jwj.community.domain.entity.member.Member;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.function.Supplier;

public interface MemberQueryRepository {

    // todo 회원 없을 때 notFoundException 처리해야 됨
    Member findByEmail(String email) throws UsernameNotFoundException;

    Member findByEmail(String email, Supplier<? extends RuntimeException> exceptionSupplier) throws RuntimeException;

    Member findByNickname(String nickname) throws UsernameNotFoundException;

}
