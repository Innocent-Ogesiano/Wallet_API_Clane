package com.wallet_api_clane.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    @NotBlank(message = "Beneficiary Email/Account number required required")
    private String beneficiaryEmailOrAccountNumber;
    private double amount;
}
