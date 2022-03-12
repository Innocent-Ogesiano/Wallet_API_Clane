package com.wallet_api_clane.exceptions;

public class UserWithEmailNotFound extends RuntimeException {
    public UserWithEmailNotFound(String message) {
        super(message);
    }
}
