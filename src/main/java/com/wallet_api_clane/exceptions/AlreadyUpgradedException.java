package com.wallet_api_clane.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AlreadyUpgradedException extends RuntimeException {
    public AlreadyUpgradedException(String s) {
        super(s);
    }
}
