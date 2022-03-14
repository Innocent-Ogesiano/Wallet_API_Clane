package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.enums.Role;
import com.wallet_api_clane.exceptions.AccountNotVerifiedException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUserDetailsServiceImplTest {
    @Mock
    private UserUtil userUtil;
    @InjectMocks
    private JwtUserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .email("email")
                .isAccountVerified(true)
                .role(Role.USER)
                .build();
    }

    @Test
    void loadUserByUsername() {
        when(userUtil.getUserWithEmail("email")).thenReturn(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername("email");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
    }

    @Test
    void givenUnverifiedUser_shouldThrow_AccountNotVerifiedException() {
        user.setAccountVerified(false);
        when(userUtil.getUserWithEmail("email")).thenReturn(user);
        assertThrows(AccountNotVerifiedException.class, ()->
                userDetailsService.loadUserByUsername("email"));
    }
}