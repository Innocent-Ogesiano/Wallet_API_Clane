package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.exceptions.AccountNotVerifiedException;
import com.wallet_api_clane.models.MyUserDetails;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsServiceImpl implements  UserDetailsService {
    private final UserUtil userUtil;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userUtil.getUserWithEmail(userEmail);
        if (!user.isAccountVerified()) {
            throw new AccountNotVerifiedException("Account is not Verified");
        } else {
            return new MyUserDetails(user);
        }
    }
}
