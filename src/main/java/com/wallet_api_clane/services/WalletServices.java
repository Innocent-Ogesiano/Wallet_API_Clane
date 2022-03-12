package com.wallet_api_clane.services;

import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;

import javax.transaction.InvalidTransactionException;

public interface WalletServices {
    void setUserWallet(User user);
    boolean topUpWallet(double amount, User user) throws InvalidAmountException, InvalidTransactionException;
    double checkWalletBalance(User user);
}
