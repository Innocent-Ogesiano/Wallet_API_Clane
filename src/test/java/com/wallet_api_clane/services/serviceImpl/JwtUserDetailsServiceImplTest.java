package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.exceptions.AccountNotVerifiedException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private JwtUserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .email("email")
                .isAccountVerified(true)
                .build();
    }

    @Test
    void loadUserByUsername() {
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername("email");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
    }

    @Test
    void givenInvalidEmail_shouldThrow_UsernameNotFoundException() {
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, ()->
                userDetailsService.loadUserByUsername("email"));
    }

    @Test
    void givenUnverifiedUser_shouldThrow_AccountNotVerifiedException() {
        user.setAccountVerified(false);
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.of(user));
        assertThrows(AccountNotVerifiedException.class, ()->
                userDetailsService.loadUserByUsername("email"));
    }
}