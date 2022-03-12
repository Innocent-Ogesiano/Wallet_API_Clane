package com.wallet_api_clane.controllers;

import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.services.WalletServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/wallet")
public class WalletController {
    private final WalletServices walletServices;

    @PutMapping("/top-up")
    public ResponseEntity<String> topUpWallet(@RequestParam double amount) throws InvalidAmountException {
        walletServices.topUpWallet(amount);
        return new ResponseEntity<>("Top up successful", HttpStatus.OK);
    }

    @GetMapping("/wallet-balance")
    public ResponseEntity<Double> checkWalletBalance() {
        return new ResponseEntity<>(walletServices.checkWalletBalance(), HttpStatus.OK);
    }
}
