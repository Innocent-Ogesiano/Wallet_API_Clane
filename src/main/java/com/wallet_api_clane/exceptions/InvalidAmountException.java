package com.wallet_api_clane.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidAmountException extends Exception {
    String message;
    HttpStatus status;

    public InvalidAmountException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
