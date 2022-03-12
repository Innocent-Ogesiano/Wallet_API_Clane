package com.wallet_api_clane.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddressDto {
    @NotBlank
    @NotNull
    private String addressLine;
    @NotBlank
    @NotNull
    private String city;
    @NotBlank
    @NotNull
    private String state;
}
