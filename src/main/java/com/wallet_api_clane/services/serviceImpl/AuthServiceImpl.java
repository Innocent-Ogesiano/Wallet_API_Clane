package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.MailDto;
import com.wallet_api_clane.dtos.SignupDto;
import com.wallet_api_clane.exceptions.ResourceAlreadyExistException;
import com.wallet_api_clane.global_constants.Constants;
import com.wallet_api_clane.models.MyUserDetails;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.repositories.WalletRepository;
import com.wallet_api_clane.services.AuthServices;
import com.wallet_api_clane.services.MailService;
import com.wallet_api_clane.utils.JwtTokenUtil;
import com.wallet_api_clane.utils.ResourceClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthServices {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ResourceClass resourceClass;

    @Override
    public void registerNewUser(SignupDto signupDto) throws MessagingException {
        Optional<User> optionalUser = userRepository
                .findUserByEmailOrPhoneNumber(signupDto.getEmail(), signupDto.getPhoneNumber());
        if (optionalUser.isPresent())
            throw new ResourceAlreadyExistException("Email/phone number"
                    + Constants.RESOURCE_ALREADY_EXIST);
        saveUser(signupDto);
    }

    @Override
    public void verifyAccount(String token) {
        String email = jwtTokenUtil.getUserEmailFromToken(token);
        User user = resourceClass.getUserWithEmail(email);
        user.setAccountVerified(true);
        userRepository.save(user);
    }

    private void saveUser(SignupDto signupDto) throws MessagingException {
        User newUser = mapper.map(signupDto, User.class);
        newUser.setAccountNumber(signupDto.getPhoneNumber().substring(1));
        newUser.setAccountVerified(false);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        setUserWallet(newUser);
        userRepository.save(newUser);
        sendVerificationMail(newUser);
    }

    private void setUserWallet(User newUser) {
        Wallet wallet = new Wallet(0);
        walletRepository.save(wallet);
        newUser.setWallet(wallet);
    }

    private void sendVerificationMail(User user) throws MessagingException {
        UserDetails userDetails = new MyUserDetails(user);
        String token = jwtTokenUtil.generateToken(userDetails);
        String content = "Thank you for signing up to the platform, " +
                "kindly click on the link below to activate your account : \n" +
                Constants.BASE_URL + "api/auth/account-verification/" + token;
        MailDto mailDto = new MailDto();
        mailDto.setBody(content);
        mailDto.setSubject("Account Verification");
        mailDto.setTo(user.getEmail());
        mailService.sendMail(mailDto);
    }
}
