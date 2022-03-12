package com.wallet_api_clane.dtos;

import com.wallet_api_clane.enums.TransactionStatus;
import com.wallet_api_clane.enums.TransactionType;
import com.wallet_api_clane.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDto {
    private User user;
    private double amount;
    private TransactionType type;
    private TransactionStatus status;
}
