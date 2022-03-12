package com.wallet_api_clane.utils;

import com.wallet_api_clane.exceptions.UserWithEmailNotFound;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.wallet_api_clane.global_constants.Constants.USER_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceClass {
    private final UserRepository userRepository;

    public static String getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    public User getUserWithEmail (String email) {
        return userRepository.findUserByEmail(email).orElseThrow(()->
                new UserWithEmailNotFound(USER_NOT_FOUND));
    }
}
