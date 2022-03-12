package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.WalletServices;
import com.wallet_api_clane.utils.ResourceClass;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.wallet_api_clane.global_constants.Constants.INVALID_AMOUNT;
import static com.wallet_api_clane.utils.ResourceClass.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletServices {
    private final UserRepository userRepository;
    private final ResourceClass resourceClass;
    private final WalletRepository walletRepository;

    @Override
    public void topUpWallet(double amount) throws InvalidAmountException {
        String email = getAuthenticatedUser();
        User user = resourceClass.getUserWithEmail(email);
        if (amount > 0) {
            Wallet wallet = user.getWallet();
            wallet.setWalletBalance(wallet.getWalletBalance() + amount);
            walletRepository.save(wallet);
            user.setWallet(wallet);
            userRepository.save(user);
        } else
            throw new InvalidAmountException(INVALID_AMOUNT, HttpStatus.BAD_REQUEST);
    }

    @Override
    public double checkWalletBalance() {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        return user.getWallet().getWalletBalance();
    }
}
