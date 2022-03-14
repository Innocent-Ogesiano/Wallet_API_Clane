package com.wallet_api_clane.services;

import com.wallet_api_clane.dtos.MailDto;
import com.wallet_api_clane.models.User;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface MailService {
    void sendMail(MailDto mailDto) throws MessagingException;
    void sendVerificationMail(User user) throws MessagingException;
}
