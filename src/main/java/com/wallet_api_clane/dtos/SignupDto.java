package com.wallet_api_clane.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Getter
@Setter
public class SignupDto {
    @Email(message = "Must be a valid email address")
    private String email;
    @NotBlank
    @Size(min = 11, max = 11)
//    @Pattern(regexp = "[0-9]", message = "Enter a valid phone number")
    private String phoneNumber;
    @NotBlank
    private String password;
}
