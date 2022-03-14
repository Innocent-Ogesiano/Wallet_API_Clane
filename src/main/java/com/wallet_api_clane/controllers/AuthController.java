package com.wallet_api_clane.controllers;

import com.wallet_api_clane.dtos.LoginDto;
import com.wallet_api_clane.dtos.SignupDto;
import com.wallet_api_clane.response.AuthResponseDto;
import com.wallet_api_clane.services.AuthServices;
import com.wallet_api_clane.services.serviceImpl.JwtUserDetailsServiceImpl;
import com.wallet_api_clane.utils.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("""
            Register new User with valid email and phone number.
            Passwords must contain at least 8 characters.
            Passwords must contain at least one uppercase character,\040
            one lowercase character, one special character, and one number.""")
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
    public ResponseEntity<AuthResponseDto> createAuthenticationToken(@RequestBody LoginDto loginDto) {
        authenticateUser(loginDto, authenticationManager);
        return generateJWTToken(loginDto);
    }

    private void authenticateUser(@RequestBody LoginDto loginDto, AuthenticationManager authenticationManager) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (DisabledException | BadCredentialsException e) {
            log.error(e.getMessage());
        }

    }

    private ResponseEntity<AuthResponseDto> generateJWTToken(LoginDto loginDto) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        final String jwtToken = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDto(jwtToken));
    }
}
