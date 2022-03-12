package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.MailDto;
import com.wallet_api_clane.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(MailDto mailDto) throws MessagingException {
        MimeMessage message  = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(mailDto.getTo());
        helper.setSubject(mailDto.getSubject());
        helper.setText(mailDto.getBody(), true);
        mailSender.send(message);

    }
}
