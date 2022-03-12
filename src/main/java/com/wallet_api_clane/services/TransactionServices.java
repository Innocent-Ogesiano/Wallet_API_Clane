package com.wallet_api_clane.services;

import com.wallet_api_clane.dtos.TransactionDto;
import com.wallet_api_clane.models.User;

public interface TransactionServices {
    void saveNewTransaction(TransactionDto transactionDto);
    double checkTransactionsPerDay(User user);
}
