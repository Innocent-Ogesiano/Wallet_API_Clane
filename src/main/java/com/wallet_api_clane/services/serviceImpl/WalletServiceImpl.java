package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.TransactionDto;
import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.services.WalletServices;
import com.wallet_api_clane.utils.ResourceClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.InvalidTransactionException;

import static com.wallet_api_clane.enums.TransactionStatus.APPROVED;
import static com.wallet_api_clane.enums.TransactionStatus.DECLINED;
import static com.wallet_api_clane.enums.TransactionType.DEPOSIT;
import static com.wallet_api_clane.global_constants.Constants.INVALID_AMOUNT;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletServices {
    private final UserRepository userRepository;
    private final ResourceClass resourceClass;
    private final WalletRepository walletRepository;
    private final TransactionServices transactionServices;

    @Override
    public void setUserWallet(User user) {
        Wallet wallet = new Wallet(0);
        walletRepository.save(wallet);
        user.setWallet(wallet);
    }

    @Override
    public boolean topUpWallet(double amount, User user) throws InvalidAmountException, InvalidTransactionException {
        if (amount > 0) {
            Object balanceLimit = resourceClass.getBalanceLimit(user);
            Wallet wallet = setUserWalletBalance(amount, user, balanceLimit);

            user.setWallet(wallet);
            userRepository.save(user);
            return true;
        } else
            throw new InvalidAmountException(INVALID_AMOUNT);
    }

    private Wallet setUserWalletBalance(double amount, User user, Object balanceLimit) throws InvalidTransactionException {
        Wallet wallet;
        if (!balanceLimit.equals("UNLIMITED")) {
            double limit = (double) balanceLimit;
            double userBalance = user.getWallet().getWalletBalance() + amount;
            if (userBalance <= limit) {
                wallet = setWallet(amount, user);
            } else {
                TransactionDto transactionDto = new TransactionDto(user, amount, DEPOSIT, DECLINED);
                transactionServices.saveNewTransaction(transactionDto);
                throw new InvalidTransactionException("Invalid transaction, kindly upgrade your account");
            }
        } else {
            wallet = setWallet(amount, user);
        }
        return wallet;
    }

    private Wallet setWallet(double amount, User user) {
        Wallet wallet = user.getWallet();
        wallet.setWalletBalance(wallet.getWalletBalance() + amount);
        walletRepository.save(wallet);
        TransactionDto transactionDto = new TransactionDto(user, amount, DEPOSIT, APPROVED);
        transactionServices.saveNewTransaction(transactionDto);
        return wallet;
    }

    @Override
    public double checkWalletBalance(User user) {
        return user.getWallet().getWalletBalance();
    }
}
