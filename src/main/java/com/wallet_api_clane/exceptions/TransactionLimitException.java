package com.wallet_api_clane.exceptions;

public class TransactionLimitException extends RuntimeException {
    public TransactionLimitException(String s) {
        super(s);
    }
}
