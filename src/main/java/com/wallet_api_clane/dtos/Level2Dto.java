package com.wallet_api_clane.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Level2Dto {
    @NotBlank(message = "First name required")
    private String firstName;
    @NotBlank(message = "Last name required")
    private String lastName;
    @NotBlank(message = "BVN required")
    @Pattern(regexp = "^\\d{11}$", message = "Enter a valid BVN")
    private String bvn;
}
