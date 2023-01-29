package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.RefreshToken;
import com.jwj.community.domain.repository.RefreshTokenRepository;
import com.jwj.community.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public void createRefreshToken(RefreshToken refreshToken, String email){
        Member savedMember = memberRepository.findByEmail(email);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        savedRefreshToken.setMember(savedMember);
        savedMember.changeRefreshToken(savedRefreshToken);
    }
}
