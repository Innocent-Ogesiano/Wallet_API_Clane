package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.utils.ResourceClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ResourceClass resourceClass;
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
                .wallet(new Wallet(0))
                .build();
    }

    @Test
    void topUpWallet() throws InvalidAmountException {
        when(resourceClass.getBalanceLimit(user)).thenReturn(50000.00);
        walletService.topUpWallet(20000, user);
        assertEquals(20000, user.getWallet().getWalletBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenInvalidAmount_shouldThrow_InvalidAmountException() {
        assertThrows(InvalidAmountException.class, ()-> walletService.topUpWallet(0, user));
    }

    @Test
    void checkWalletBalance() {
        double amount = walletService.checkWalletBalance(user);
        assertEquals(user.getWallet().getWalletBalance(), amount);
    }
}