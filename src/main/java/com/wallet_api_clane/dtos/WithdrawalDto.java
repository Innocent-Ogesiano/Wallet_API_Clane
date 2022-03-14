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
public class WithdrawalDto {
    @NotBlank(message = "Withdrawal Amount required")
    private double withdrawalAmount;
}
