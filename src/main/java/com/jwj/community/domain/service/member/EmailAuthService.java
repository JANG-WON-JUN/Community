package com.jwj.community.domain.service.member;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender javaMailSender;


    public void sendAuthMail() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo("w32807@naver.com"); // 메일 수신자
        mimeMessageHelper.setSubject("스프링에서 메일보내기"); // 메일 제목
        mimeMessageHelper.setText("123214@$@$AAAsaf --- 내용");

        javaMailSender.send(mimeMessage);
    }
}
