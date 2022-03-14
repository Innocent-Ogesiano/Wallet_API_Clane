package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.SignupDto;
import com.wallet_api_clane.exceptions.ResourceAlreadyExistException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.services.UserServices;
import com.wallet_api_clane.utils.JwtTokenUtil;
import com.wallet_api_clane.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserUtil userUtil;
    @Mock
    private UserServices userServices;
    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
    }

    @Test
    void registerNewUser() throws MessagingException {
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("email");
        signupDto.setPhoneNumber("phone");
        authService.registerNewUser(signupDto);
        verify(userRepository, times(1))
                .findUserByEmailOrPhoneNumber(signupDto.getEmail(), signupDto.getPhoneNumber());
        verify(userServices, times(1)).saveNewUser(signupDto);
    }

    @Test
    void givenExistingEmail_shouldThrow_ResourceAlreadyExistException() {
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("email");
        signupDto.setPhoneNumber("phone");
        when(userRepository.findUserByEmailOrPhoneNumber("email", "phone")).thenReturn(Optional.of(user));
        assertThrows(ResourceAlreadyExistException.class,
                ()-> authService.registerNewUser(signupDto));
    }

    @Test
    void verifyAccount() {
        String email = "og@gmail.com";
        when(jwtTokenUtil.getUserEmailFromToken(anyString())).thenReturn(email);
        when(userUtil.getUserWithEmail(email)).thenReturn(user);
        authService.verifyAccount("token");
        assertTrue(user.isAccountVerified());
        verify(userRepository, times(1)).save(any(User.class));
    }
}