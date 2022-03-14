package com.wallet_api_clane.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
@Getter
@Setter
@Builder
public class Level3Dto {
    private AddressDto address;
    @NotBlank(message = "NIN required")
    private String nin;
}
