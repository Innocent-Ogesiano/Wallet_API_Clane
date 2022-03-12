package com.wallet_api_clane.controllers;

import com.wallet_api_clane.dtos.Level2Dto;
import com.wallet_api_clane.dtos.Level3Dto;
import com.wallet_api_clane.dtos.TransferDto;
import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.services.UserServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import javax.transaction.InvalidTransactionException;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private final UserServices userServices;

    @PutMapping("/wallet/top-up")
    public ResponseEntity<String> topUpWallet(@RequestParam double amount) throws InvalidAmountException, InvalidTransactionException {
        userServices.deposit(amount);
        return new ResponseEntity<>("Deposit successful", HttpStatus.OK);
    }

    @GetMapping("/wallet-balance")
    public ResponseEntity<Double> checkWalletBalance() {
        return new ResponseEntity<>(userServices.checkBalance(), HttpStatus.OK);
    }

    @PutMapping("/upgrade/level-2")
    public ResponseEntity<String> upgradeToLevel2(@Valid @RequestBody Level2Dto level2Dto) {
        userServices.upgradeToLevel2(level2Dto);
        return new ResponseEntity<>("Successfully upgraded to level 2", HttpStatus.OK);
    }

    @PutMapping("/upgrade/level-3")
    public ResponseEntity<String> upgradeToLevel3(@Valid @RequestBody Level3Dto level3Dto) {
        userServices.upgradeToLevel3(level3Dto);
        return new ResponseEntity<>("Successfully upgraded to level 3", HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferToBeneficiary(@Valid @RequestBody TransferDto transferDto) throws InvalidAmountException, InvalidTransactionException, InsufficientResourcesException {
        userServices.transferMoneyToAnotherUser(transferDto);
        return new ResponseEntity<>("Transfer Successful", HttpStatus.OK);
    }
}
