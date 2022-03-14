package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.*;
import com.wallet_api_clane.exceptions.*;
import com.wallet_api_clane.models.Address;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.AddressRepository;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.services.MailService;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.services.UserServices;
import com.wallet_api_clane.services.WalletServices;
import com.wallet_api_clane.utils.ResourceClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.naming.InsufficientResourcesException;
import javax.transaction.InvalidTransactionException;

import static com.wallet_api_clane.enums.KYCLevel.*;
import static com.wallet_api_clane.enums.TransactionStatus.APPROVED;
import static com.wallet_api_clane.enums.TransactionStatus.DECLINED;
import static com.wallet_api_clane.enums.TransactionType.TRANSFER;
import static com.wallet_api_clane.enums.TransactionType.WITHDRAWAL;
import static com.wallet_api_clane.global_constants.Constants.*;
import static com.wallet_api_clane.utils.ResourceClass.getAuthenticatedUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserServices {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final WalletServices walletServices;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final ResourceClass resourceClass;
    private final AddressRepository addressRepository;
    private final TransactionServices transactionServices;

    @Override
    public void saveNewUser(SignupDto signupDto) throws MessagingException {
        User newUser = mapper.map(signupDto, User.class);
        newUser.setAccountNumber(signupDto.getPhoneNumber().substring(1));
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        walletServices.setNewUserWallet(newUser);
        userRepository.save(newUser);
        mailService.sendVerificationMail(newUser);
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException, InvalidTransactionException {
        String email = getAuthenticatedUser();
        User user = resourceClass.getUserWithEmail(email);
        boolean status = walletServices.topUpWallet(amount, user);
        if (!status)
            throw new InvalidTransactionException("Invalid transaction, kindly upgrade your account");
    }

    @Override
    public double checkBalance() {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        return walletServices.checkWalletBalance(user);
    }

    @Override
    public void upgradeToLevel2(Level2Dto level2Dto) {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        if (user.getKycLevel().equals(LEVEL_1)) {
            mapper.map(level2Dto, user);
            user.setKycLevel(LEVEL_2);
            userRepository.save(user);
        } else
            throw new AlreadyUpgradedException(ALREADY_UPGRADED + user.getKycLevel());
    }

    @Override
    public void upgradeToLevel3(Level3Dto level3Dto) {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        if (!user.getKycLevel().equals(LEVEL_3) && user.getKycLevel().equals(LEVEL_2)) {
            Address address = mapper.map(level3Dto, Address.class);
            addressRepository.save(address);
            mapper.map(level3Dto, user);
            user.setAddress(address);
            user.setKycLevel(LEVEL_3);
            userRepository.save(user);
        } else if (user.getKycLevel().equals(LEVEL_3)){
            log.info(ALREADY_UPGRADED + user.getKycLevel());
            throw new AlreadyUpgradedException(ALREADY_UPGRADED + user.getKycLevel());
        } else
            throw new NotQualifiedException("Upgrade to level 2 first");
    }

    @Override
    public void transferMoneyToAnotherUser(TransferDto transferDto) throws InvalidAmountException, InvalidTransactionException, InsufficientResourcesException {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        double walletBalance = user.getWallet().getWalletBalance();
        double transferAmount = transferDto.getAmount();
        double transactionLimit = resourceClass.getTransactionLimit(user);
        double transactionAmount = transactionServices.checkTransactionsForTheDay(user) + transferAmount;
        if (!(transferAmount > 0) || !(walletBalance >= transferAmount) || !(transactionAmount <= transactionLimit)) {
            if (walletBalance < transferAmount) {
                log.error(INSUFFICIENT_BALANCE);
                throw new InsufficientResourcesException(INSUFFICIENT_BALANCE);
            }
            else if (transactionAmount > transactionLimit) {
                log.error(LIMIT_REACHED);
                throw new TransactionLimitException(LIMIT_REACHED);
            }
            else {
                log.error(INVALID_AMOUNT);
                throw new InvalidAmountException(INVALID_AMOUNT);
            }
        } else {
            transferToBeneficiary(transferDto, user);
        }
    }

    private void transferToBeneficiary(TransferDto transferDto, User user) throws InvalidAmountException, InvalidTransactionException {
        User beneficiary = userRepository.findUserByEmailOrAccountNumber(transferDto.getBeneficiaryEmailOrAccountNumber())
                .orElseThrow(()-> new UserWithEmailNotFound(USER_NOT_FOUND));
        TransactionDto transactionDto = creditBeneficiary(transferDto, user, beneficiary);
        if (transactionDto.getStatus().equals(DECLINED)) {
            transactionServices.saveNewTransaction(transactionDto);
            throw new TransactionDeclinedException("Transaction not successful");
        }
        transactionServices.saveNewTransaction(transactionDto);
    }

    private TransactionDto creditBeneficiary(TransferDto transferDto, User user, User beneficiary) throws InvalidAmountException, InvalidTransactionException {
        boolean status = walletServices.topUpWallet(transferDto.getAmount(), beneficiary);
        TransactionDto transactionDto;
        double transferAmount = transferDto.getAmount();
        if (status) {
            double walletBalance = user.getWallet().getWalletBalance();
            walletBalance = walletBalance - transferAmount;
            user.getWallet().setWalletBalance(walletBalance);
            userRepository.save(user);
            transactionDto = new TransactionDto(user, transferAmount, TRANSFER, APPROVED);
        } else
            transactionDto = new TransactionDto(user, transferAmount, TRANSFER, DECLINED);
        return transactionDto;
    }

    @Override
    public void withdrawFromWallet(WithdrawalDto withdrawalDto) throws InvalidAmountException, InsufficientResourcesException {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        double walletBalance = user.getWallet().getWalletBalance();
        double withdrawalAmount = withdrawalDto.getWithdrawalAmount();
        double transactionLimit = resourceClass.getTransactionLimit(user);
        double transactionAmount = transactionServices.checkTransactionsForTheDay(user) + withdrawalDto.getWithdrawalAmount();
        if (!(withdrawalAmount > 0) || !(walletBalance >= withdrawalAmount) || !(transactionAmount <= transactionLimit)) {
            TransactionDto transactionDto =
                    new TransactionDto(user, withdrawalAmount, WITHDRAWAL, DECLINED);
            transactionServices.saveNewTransaction(transactionDto);
            if (withdrawalAmount <= 0)
                throw new InvalidAmountException(INVALID_AMOUNT);
            else if (walletBalance < withdrawalAmount)
                throw new InsufficientResourcesException(INSUFFICIENT_BALANCE);
            else
                throw new TransactionLimitException(LIMIT_REACHED);
        }
        else
            walletServices.withdrawFromWallet(withdrawalAmount, user);
    }
}
