package com.jwj.community.web.controller.member;

import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.common.result.Result;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.MemberEmail;
import com.jwj.community.web.dto.member.request.MemberNickname;
import com.jwj.community.web.validator.MemberCreateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.Boolean.FALSE;
import static java.util.Locale.getDefault;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberCreateValidator memberCreateValidator;
    private final MessageSource messageSource;

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
    public ResponseEntity<Result<Boolean>> isDuplicateEmail(@Valid @RequestBody MemberEmail member, BindingResult bindingResult) throws BindException {
        if(memberService.findByEmail(member.getEmail()) != null){
            bindingResult.rejectValue("email", "", messageSource.getMessage("duplicate.email", null, getDefault()));
            throw new BindException(bindingResult);
        }

        Result<Boolean> result = Result.<Boolean>builder()
                .data(FALSE)
                .build();

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/api/join/nicknameCheck")
    public ResponseEntity<Result<Boolean>> isDuplicateNickname(@RequestBody MemberNickname member, BindingResult bindingResult) throws BindException {
        if(memberService.findByNickname(member.getNickname()) != null){
            bindingResult.rejectValue("nickname", "", messageSource.getMessage("duplicate.nickname", null, getDefault()));
            throw new BindException(bindingResult);
        }

        Result<Boolean> result = Result.<Boolean>builder()
                .data(FALSE)
                .build();

        return ResponseEntity.ok().body(result);
    }
}
