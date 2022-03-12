package com.wallet_api_clane.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserWithEmailNotFound extends RuntimeException {
    public UserWithEmailNotFound(String message) {
        super(message);
    }
}
