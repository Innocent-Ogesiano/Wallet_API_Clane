package com.wallet_api_clane.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    @NotNull
    @NotBlank
    private String beneficiaryEmailOrAccountNumber;
    private double amount;
}
