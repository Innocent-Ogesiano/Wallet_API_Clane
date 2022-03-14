package com.wallet_api_clane.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AddressDto {
    @NotBlank(message = "Address line required")
    private String addressLine;
    @NotBlank(message = "City required")
    private String city;
    @NotBlank(message = "State required")
    private String state;
}
