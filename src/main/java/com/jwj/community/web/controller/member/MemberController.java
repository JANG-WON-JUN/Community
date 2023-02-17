package com.jwj.community.web.controller.member;

import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.dto.member.request.MemberCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/join")
    public ResponseEntity<String> join(@Valid @RequestBody MemberCreate memberCreate){
        memberService.createMember(memberCreate.toEntity(), memberCreate.getPassword().toEntity());

        return ResponseEntity.ok().body(null);
    }

}
