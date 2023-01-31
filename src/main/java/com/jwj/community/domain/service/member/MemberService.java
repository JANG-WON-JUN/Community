package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.domain.entity.member.auth.MemberRoles;
import com.jwj.community.domain.entity.member.auth.Role;
import com.jwj.community.domain.repository.member.MemberRepository;
import com.jwj.community.domain.repository.member.auth.RoleRepository;
import com.jwj.community.domain.service.member.auth.MemberRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jwj.community.domain.enums.Roles.ROLE_MEMBER;
import static com.jwj.community.domain.enums.State.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordService passwordService;
    private final MemberRolesService memberRolesService;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void createMember(Member member, Password password) {
        Role role = roleRepository.findByRoleName(ROLE_MEMBER);

        // 1. memberstate 기본값 주기
        member.changeState(ACTIVE);
        member.levelUp(); // 기본 레벨 부여
        password.encodePassword(passwordEncoder);
        // todo MemberRoles 엔티티에 데이터 저장 후 member에 role 부여해야 됨
        // todo 이메일 인증기능 EmailAuth 엔티티에 데이터 저장 후 매핑해야 됨
        // todo 회원가입 시 level1 부여 후 로그에 insert 해줘야 됨
        // password & member 연관관계
        // 2. password 연관관계 매핑
        member.setPassword(password);
        password.setMember(member);

        Member savedMember = memberRepository.save(member);
        MemberRoles savedMemberRoles = memberRolesService.createMemberRoles(savedMember, role);

        savedMember.addRole(savedMemberRoles);

        passwordService.createPassword(password);
    }

    public Member findByEmail(String email) {
        // todo Member없을 때 CustomException 구축해야 함
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    public void addMemberPoint(Member member) {
        member.addLevelPoint();
        if(member.levelUp()){
            // 레벨업 성공 시 기록
            // 가능하면 레벨 업 후 memberLog에 기록 후 member의 level도 업데이트 해줘야 함
        }
    }
}
