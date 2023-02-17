package com.jwj.community.web.validator;

import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.dto.member.request.BirthDayCreate;
import com.jwj.community.web.dto.member.request.MemberCreate;
import com.jwj.community.web.dto.member.request.PasswordCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static java.util.Locale.getDefault;

@Component
@RequiredArgsConstructor
public class MemberCreateValidator implements Validator {

    private final MessageSource messageSource;
    private final MemberService memberService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(MemberCreate.class);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        MemberCreate memberCreate = (MemberCreate) target;

        isDuplicateEmail(memberCreate, errors);
        isDuplicateNickname(memberCreate, errors);
        isMatchedPassword(memberCreate, errors);
        isValidBirthday(memberCreate, errors);
    }

    // findByEmail 실행 후 UsernameNotFoundException가 발생했을 때 그냥 넘어가도록 try~catch로 처리
    private void isDuplicateEmail(MemberCreate memberCreate, Errors errors) {
        try {
            memberService.findByEmail(memberCreate.getEmail());
            errors.rejectValue("email", "",
                    messageSource.getMessage("duplicate.email", null, getDefault()));
        }catch (UsernameNotFoundException e){}
    }

    // findByNickname 실행 후 UsernameNotFoundException가 발생했을 때 그냥 넘어가도록 try~catch로 처리
    private void isDuplicateNickname(MemberCreate memberCreate, Errors errors) {
        try {
            memberService.findByNickname(memberCreate.getNickname());
            errors.rejectValue("nickname", "",
                    messageSource.getMessage("duplicate.nickname", null, getDefault()));
        }catch (UsernameNotFoundException e){}
    }

    private void isMatchedPassword(MemberCreate memberCreate, Errors errors) {
        PasswordCreate passwordCreate = memberCreate.getPassword();

        if(!passwordCreate.isMatchedPasswordAndConfirmPassword()){
            errors.rejectValue("password", "",
                    messageSource.getMessage("confirm.pwd.not.match", null, getDefault()));
        }
    }

    private void isValidBirthday(MemberCreate memberCreate, Errors errors) {
        BirthDayCreate birthDay = memberCreate.getBirthDay();

        if(!birthDay.isValidBirthDay()){
            errors.rejectValue("birthDay", "",
                    messageSource.getMessage("confirm.birthDay", null, getDefault()));
        }
    }
}
