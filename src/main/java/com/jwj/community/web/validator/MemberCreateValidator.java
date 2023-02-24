package com.jwj.community.web.validator;

import com.jwj.community.domain.service.member.MemberService;
import com.jwj.community.web.dto.member.request.MemberCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.jwj.community.utils.CommonUtils.isEmpty;
import static com.jwj.community.utils.CommonUtils.isValidDate;
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

    private boolean isDuplicateEmail(MemberCreate memberCreate, Errors errors) {
        if(memberService.findByEmail(memberCreate.getEmail()) != null) {
            errors.rejectValue("email", "",
                        messageSource.getMessage("duplicate.email", null, getDefault()));
            return true;
        }

        return false;
    }

    private boolean isDuplicateNickname(MemberCreate memberCreate, Errors errors) {
        if(memberService.findByNickname(memberCreate.getNickname()) != null) {
            errors.rejectValue("nickname", "",
                    messageSource.getMessage("duplicate.nickname", null, getDefault()));
            return true;
        }

        return false;
    }

    private boolean isMatchedPassword(MemberCreate memberCreate, Errors errors) {
        String password = memberCreate.getPassword();
        String confirmPassword = memberCreate.getConfirmPassword();

        if(isEmpty(password) || isEmpty(confirmPassword) || !password.equals(confirmPassword)){
            errors.rejectValue("password", "",
                    messageSource.getMessage("confirm.pwd.not.match", null, getDefault()));

            return false;
        }
        return true;
    }

    private boolean isValidBirthday(MemberCreate memberCreate, Errors errors) {
        if(!isValidDate(memberCreate.getYear(), memberCreate.getMonth(), memberCreate.getDay())){
            errors.reject("confirm.birthDay", messageSource.getMessage("confirm.birthDay", null, getDefault()));
            return false;
        }
        return true;
    }
}
