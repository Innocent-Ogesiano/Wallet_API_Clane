package com.wallet_api_clane.controllers;

import com.wallet_api_clane.utils.JwtTokenUtil;
import com.wallet_api_clane.dtos.AuthResponseDto;
import com.wallet_api_clane.dtos.LoginDto;
import com.wallet_api_clane.dtos.SignupDto;
import com.wallet_api_clane.services.AuthServices;
import com.wallet_api_clane.services.serviceImpl.JwtUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServices authServices;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsServiceImpl userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerNewUser(@Valid @RequestBody SignupDto signupDto) throws MessagingException {
        authServices.registerNewUser(signupDto);
        return new ResponseEntity<>(
                "Registration Successful, check your mail to verify your email", HttpStatus.CREATED);
    }

    @GetMapping(value = "/account-verification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authServices.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponseDto> createAuthenticationToken(@RequestBody LoginDto loginDto) throws IOException {
        authenticateUser(loginDto, authenticationManager);
        return generateJWTToken(loginDto);
    }

    private void authenticateUser(@RequestBody LoginDto loginDto, AuthenticationManager authenticationManager) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (loginDto.getEmail(), loginDto.getPassword()));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);
    }

    private ResponseEntity<AuthResponseDto> generateJWTToken(LoginDto loginDto) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        final String jwtToken = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDto(jwtToken));
    }
}