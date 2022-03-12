package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.SignupDto;
import com.wallet_api_clane.exceptions.ResourceAlreadyExistException;
import com.wallet_api_clane.global_constants.Constants;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.services.AuthServices;
import com.wallet_api_clane.services.UserServices;
import com.wallet_api_clane.utils.JwtTokenUtil;
import com.wallet_api_clane.utils.ResourceClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthServices {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ResourceClass resourceClass;
    private final UserServices userServices;

    @Override
    public void registerNewUser(SignupDto signupDto) throws MessagingException {
        Optional<User> optionalUser = userRepository
                .findUserByEmailOrPhoneNumber(signupDto.getEmail(), signupDto.getPhoneNumber());
        if (optionalUser.isPresent())
            throw new ResourceAlreadyExistException("Email/phone number"
                    + Constants.RESOURCE_ALREADY_EXIST);
        userServices.saveNewUser(signupDto);
    }

    @Override
    public void verifyAccount(String token) {
        String email = jwtTokenUtil.getUserEmailFromToken(token);
        User user = resourceClass.getUserWithEmail(email);
        user.setAccountVerified(true);
        userRepository.save(user);
    }

}
