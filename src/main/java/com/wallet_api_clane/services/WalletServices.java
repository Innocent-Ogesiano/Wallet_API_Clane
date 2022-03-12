package com.wallet_api_clane.services;

import com.wallet_api_clane.exceptions.InvalidAmountException;

public interface WalletServices {
    void topUpWallet(double amount) throws InvalidAmountException;
    double checkWalletBalance();
}
