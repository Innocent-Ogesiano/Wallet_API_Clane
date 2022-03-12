package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.exceptions.AccountNotVerifiedException;
import com.wallet_api_clane.models.MyUserDetails;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.wallet_api_clane.global_constants.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsServiceImpl implements  UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<User> userModel = userRepository.findUserByEmail(userEmail);
        User user = userModel.orElseThrow(() ->
              new UsernameNotFoundException(USER_NOT_FOUND));

        if(user.isAccountVerified())
            return new MyUserDetails(user);
        else
            throw new AccountNotVerifiedException("Account is not Verified");
    }
}
