package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.Member;
import com.jwj.community.domain.entity.member.MemberLevelLog;
import com.jwj.community.domain.entity.member.Password;
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
    private final MemberLevelLogService memberLevelLogService;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void createMember(Member member, Password password) {
        Role role = roleRepository.findByRoleName(ROLE_MEMBER);

        member.changeState(ACTIVE);
        member.levelUp(); // 기본 레벨 부여
        member.setPassword(password);
        password.encodePassword(passwordEncoder);
        // todo MemberRoles 엔티티에 데이터 저장 후 member에 role 부여해야 됨
        // todo 이메일 인증기능 EmailAuth 엔티티에 데이터 저장 후 매핑해야 됨
        // todo 회원가입 시 level1 부여 후 로그에 insert 해줘야 됨
        // password & member 연관관계
        // 2. password 연관관계 매핑
        //password.setMember(member);

        Member savedMember = memberRepository.save(member);
        savedMember.addRole(memberRolesService.createMemberRoles(savedMember, role));

        passwordService.createPassword(password);

        MemberLevelLog memberLevelLog = MemberLevelLog.builder()
                .levelPoint(savedMember.getLevelPoint())
                .level(savedMember.getLevel())
                .build();

        memberLevelLogService.createLevelLog(savedMember, memberLevelLog);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void addMemberPoint(Member member) {
        member.addLevelPoint();
        if(member.levelUp()){
            MemberLevelLog memberLevelLog = MemberLevelLog.builder()
                    .levelPoint(member.getLevelPoint())
                    .level(member.getLevel())
                    .build();
            memberLevelLogService.createLevelLog(member, memberLevelLog);
        }
    }

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }
}
