package com.jwj.community.domain.service.member;

import com.jwj.community.domain.entity.member.Password;
import com.jwj.community.domain.repository.member.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;

    public void createPassword(Password password){
        password.extendBeChangedDate();
        passwordRepository.save(password);
    }
}
