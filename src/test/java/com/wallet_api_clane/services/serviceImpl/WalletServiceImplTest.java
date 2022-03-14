package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.TransactionDto;
import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserUtil userUtil;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionServices transactionServices;
    @InjectMocks
    private WalletServiceImpl walletService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .email("og@gmail.com")
                .build();
    }

    @Test
    void setNewUserWallet() {
        walletService.setNewUserWallet(user);
        assertThat(user.getWallet()).isNotNull();
        assertEquals(0.00, user.getWallet().getWalletBalance());
        verify(walletRepository, times(1))
                .save(any(Wallet.class));
    }

    @Test
    void topUpWallet() throws InvalidAmountException {
        walletService.setNewUserWallet(user);
        when(userUtil.getBalanceLimit(user)).thenReturn(50000.00);
        boolean status = walletService.topUpWallet(20000, user);
        assertTrue(status);
        assertEquals(20000, user.getWallet().getWalletBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenInvalidAmount_shouldThrow_InvalidAmountException() {
        assertThrows(InvalidAmountException.class, ()-> walletService.topUpWallet(0, user));
    }

    @Test
    void checkWalletBalance() {
        walletService.setNewUserWallet(user);
        double amount = walletService.checkWalletBalance(user);
        assertEquals(user.getWallet().getWalletBalance(), amount);
    }

    @Test
    void withdrawFromWallet() {
        user.setWallet(new Wallet(50000.00));
        walletService.withdrawFromWallet(20000.00, user);
        assertEquals(30000.00, user.getWallet().getWalletBalance());
        verify(transactionServices, times(1))
                .saveNewTransaction(any(TransactionDto.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
}