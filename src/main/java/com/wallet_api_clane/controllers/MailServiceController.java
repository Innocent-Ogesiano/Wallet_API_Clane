package com.wallet_api_clane.controllers;

import com.wallet_api_clane.dtos.MailDto;
import com.wallet_api_clane.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
public class MailServiceController {

    private final MailService mailService;

    @PostMapping("/send-notification")
    public void sendNotification(@RequestBody MailDto mailDto) throws MessagingException {
        mailService.sendMail(mailDto);
    }
}
