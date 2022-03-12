package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.utils.ResourceClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ResourceClass resourceClass;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private WalletServiceImpl walletService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("og@gmail.com");
        user.setWallet(new Wallet(0));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        UserDetails userDetails = withUsername("og@gmail.com").password("password").roles("USER").build();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
        when((UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(userDetails);

        when(resourceClass.getUserWithEmail("og@gmail.com")).thenReturn(user);
    }

    @Test
    void topUpWallet() throws InvalidAmountException {
        walletService.topUpWallet(20000);
        assertEquals(20000, user.getWallet().getWalletBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenInvalidAmount_shouldThrow_InvalidAmountException() {
        assertThrows(InvalidAmountException.class, ()-> walletService.topUpWallet(0));
    }

    @Test
    void checkWalletBalance() {
        double amount = walletService.checkWalletBalance();
        assertEquals(user.getWallet().getWalletBalance(), amount);
    }
}