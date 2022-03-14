package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.utils.JwtTokenUtil;
import com.wallet_api_clane.dtos.SignupDto;
import com.wallet_api_clane.exceptions.ResourceAlreadyExistException;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private MailService mailService;
    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
        wallet = new Wallet(0);
    }

    @Test
    void registerNewUser() throws MessagingException {
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("email");
        signupDto.setPhoneNumber("phone");
        when(mapper.map(signupDto, User.class)).thenReturn(user);
        authService.registerNewUser(signupDto);
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1))
                .findUserByEmailOrPhoneNumber(signupDto.getEmail(), signupDto.getPhoneNumber());
        verify(mapper, times(1)).map(signupDto, User.class);
        verify(passwordEncoder, times(1)).encode(user.getPassword());
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
}