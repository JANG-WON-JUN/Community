package com.jwj.community.web.controller.member;

import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.validator.MemberCreateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.Boolean.TRUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberCreateValidator memberCreateValidator;

    @PostMapping("/api/join")
    public ResponseEntity<String> join(@Valid @RequestBody MemberCreate memberCreate, BindingResult bindingResult) throws BindException {
        memberCreateValidator.validate(memberCreate, bindingResult);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        memberService.createMember(memberCreate.toEntity(), memberCreate.getPasswordEntity());

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/api/join/emailCheck")
    public ResponseEntity<Result<Boolean>> isEmailDuplicate(@RequestParam String email) {
        memberService.findByEmail(email);

        Result<Boolean> result = Result.<Boolean>builder()
            .data(TRUE)
            .build();

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/api/join/nicknameCheck")
    public ResponseEntity<Result<Boolean>> isNicknameDuplicate(@RequestParam String nickname) {
        memberService.findByNickname(nickname);

        Result<Boolean> result = Result.<Boolean>builder()
            .data(TRUE)
            .build();

        return ResponseEntity.ok().body(result);
    }
}
