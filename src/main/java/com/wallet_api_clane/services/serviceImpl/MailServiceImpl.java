package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.MailDto;
import com.wallet_api_clane.global_constants.Constants;
import com.wallet_api_clane.models.MyUserDetails;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.services.MailService;
import com.wallet_api_clane.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void sendMail(MailDto mailDto) throws MessagingException {
        MimeMessage message  = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(mailDto.getTo());
        helper.setSubject(mailDto.getSubject());
        helper.setText(mailDto.getBody(), true);
        mailSender.send(message);

    }

    @Override
    public void sendVerificationMail(User user) throws MessagingException {
        UserDetails userDetails = new MyUserDetails(user);
        String token = jwtTokenUtil.generateToken(userDetails);
        String content = "Thank you for signing up to the platform, " +
                "kindly click on the link below to activate your account : \n" +
                Constants.BASE_URL + "api/auth/account-verification/" + token;
        MailDto mailDto = new MailDto();
        mailDto.setBody(content);
        mailDto.setSubject("Account Verification");
        mailDto.setTo(user.getEmail());
        sendMail(mailDto);
    }
}
