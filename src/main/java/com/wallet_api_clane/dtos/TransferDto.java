package com.wallet_api_clane.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class TransferDto {
    @NotNull
    @NotBlank
    private String beneficiaryEmailOrAccountNumber;
    private double amount;
}
