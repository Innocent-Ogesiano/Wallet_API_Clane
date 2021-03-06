package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.TransactionDto;
import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.services.WalletServices;
import com.wallet_api_clane.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.wallet_api_clane.enums.TransactionStatus.APPROVED;
import static com.wallet_api_clane.enums.TransactionStatus.DECLINED;
import static com.wallet_api_clane.enums.TransactionType.DEPOSIT;
import static com.wallet_api_clane.enums.TransactionType.WITHDRAWAL;
import static com.wallet_api_clane.global_constants.Constants.INVALID_AMOUNT;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletServices {
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final WalletRepository walletRepository;
    private final TransactionServices transactionServices;

    @Override
    public void setNewUserWallet(User user) {
        Wallet wallet = new Wallet(0);
        walletRepository.save(wallet);
        user.setWallet(wallet);
    }

    @Override
    public boolean topUpWallet(double amount, User user) throws InvalidAmountException {
        if (amount > 0) {
            double balanceLimit = userUtil.getBalanceLimit(user);
            Wallet wallet = checkUserBalanceLimit(amount, user, balanceLimit);
            if (wallet != null) {
                user.setWallet(wallet);
                userRepository.save(user);
                return true;
            } else
                return false;
        }
        throw new InvalidAmountException(INVALID_AMOUNT);
    }

    private Wallet checkUserBalanceLimit(double amount, User user, double balanceLimit) {
        double userBalance = user.getWallet().getWalletBalance() + amount;
        if (userBalance <= balanceLimit) {
            return setWallet(amount, user);
        } else {
            TransactionDto transactionDto = new TransactionDto(user, amount, DEPOSIT, DECLINED);
            transactionServices.saveNewTransaction(transactionDto);
            return null;
        }
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

    @Override
    public void withdrawFromWallet(double withdrawalAmount, User user) {
        Wallet wallet = user.getWallet();
        wallet.setWalletBalance(wallet.getWalletBalance() - withdrawalAmount);
        user.setWallet(wallet);
        userRepository.save(user);
        TransactionDto transactionDto =
                new TransactionDto(user, withdrawalAmount, WITHDRAWAL, APPROVED);
        transactionServices.saveNewTransaction(transactionDto);
    }
}
