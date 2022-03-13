package com.wallet_api_clane.services;

import com.wallet_api_clane.dtos.*;
import com.wallet_api_clane.exceptions.InvalidAmountException;

import javax.mail.MessagingException;
import javax.naming.InsufficientResourcesException;
import javax.transaction.InvalidTransactionException;

public interface UserServices {
    void saveNewUser(SignupDto signupDto) throws MessagingException;
    void deposit(double amount) throws InvalidAmountException, InvalidTransactionException;
    double checkBalance();
    void upgradeToLevel2(Level2Dto level2Dto);
    void upgradeToLevel3(Level3Dto level3Dto);
    void transferMoneyToAnotherUser(TransferDto transferDto) throws InvalidAmountException, InvalidTransactionException, InsufficientResourcesException;
    void withdrawFromWallet(WithdrawalDto withdrawalDto) throws InvalidAmountException, InsufficientResourcesException;
}
