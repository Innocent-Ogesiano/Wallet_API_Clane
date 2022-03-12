package com.wallet_api_clane.utils;

import com.wallet_api_clane.exceptions.UserWithEmailNotFound;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public double getTransactionLimit(User user) {
        return switch (user.getKycLevel()) {
            case LEVEL_3 -> 5000000.00;
            case LEVEL_2 -> 200000.00;
            default -> 50000.00;
        };
    }

    public Object getBalanceLimit(User user) {
        return switch (user.getKycLevel()) {
            case LEVEL_3 -> "UNLIMITED";
            case LEVEL_2 -> 500000.00;
            default -> 50000.00;
        };
    }

    public static ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(httpStatus.value(),
                httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(httpResponse, httpStatus);
    }
}
