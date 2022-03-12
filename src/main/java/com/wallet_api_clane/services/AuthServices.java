package com.wallet_api_clane.services;

import com.wallet_api_clane.dtos.SignupDto;

import javax.mail.MessagingException;

public interface AuthServices {
    void registerNewUser(SignupDto signupDto) throws MessagingException;
    void verifyAccount(String token);
}
