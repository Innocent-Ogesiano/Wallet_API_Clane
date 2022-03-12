package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.*;
import com.wallet_api_clane.enums.KYCLevel;
import com.wallet_api_clane.exceptions.AlreadyUpgradedException;
import com.wallet_api_clane.exceptions.InvalidAmountException;
import com.wallet_api_clane.exceptions.TransactionLimitException;
import com.wallet_api_clane.exceptions.UserWithEmailNotFound;
import com.wallet_api_clane.global_constants.Constants;
import com.wallet_api_clane.models.Address;
import com.wallet_api_clane.models.MyUserDetails;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.repositories.AddressRepository;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.services.MailService;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.services.UserServices;
import com.wallet_api_clane.services.WalletServices;
import com.wallet_api_clane.utils.JwtTokenUtil;
import com.wallet_api_clane.utils.ResourceClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.naming.InsufficientResourcesException;
import javax.transaction.InvalidTransactionException;

import static com.wallet_api_clane.enums.TransactionStatus.APPROVED;
import static com.wallet_api_clane.enums.TransactionStatus.DECLINED;
import static com.wallet_api_clane.enums.TransactionType.TRANSFER;
import static com.wallet_api_clane.global_constants.Constants.USER_NOT_FOUND;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final ResourceClass resourceClass;
    private final AddressRepository addressRepository;
    private final TransactionServices transactionServices;

    @Override
    public void saveNewUser(SignupDto signupDto) throws MessagingException {
        User newUser = mapper.map(signupDto, User.class);
        newUser.setAccountNumber(signupDto.getPhoneNumber().substring(1));
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        walletServices.setUserWallet(newUser);
        userRepository.save(newUser);
        sendVerificationMail(newUser);
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException, InvalidTransactionException {
        String email = getAuthenticatedUser();
        User user = resourceClass.getUserWithEmail(email);
        walletServices.topUpWallet(amount, user);
    }

    @Override
    public double checkBalance() {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        return walletServices.checkWalletBalance(user);
    }

    @Override
    public void upgradeToLevel2(Level2Dto level2Dto) {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        mapper.map(level2Dto, user);
        user.setKycLevel(KYCLevel.LEVEL_2);
        userRepository.save(user);
    }

    @Override
    public void upgradeToLevel3(Level3Dto level3Dto) {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        if (!user.getKycLevel().equals(KYCLevel.LEVEL_3)) {
            Address address = mapper.map(level3Dto, Address.class);
            log.info(address.getCity());
            addressRepository.save(address);
            mapper.map(level3Dto, user);
            user.setAddress(address);
            user.setKycLevel(KYCLevel.LEVEL_3);
            userRepository.save(user);
        } else {
            log.info("Already upgraded to level 3");
            throw new AlreadyUpgradedException("Already upgraded to level 3");
        }
    }

    @Override
    public void transferMoneyToAnotherUser(TransferDto transferDto) throws InvalidAmountException, InvalidTransactionException, InsufficientResourcesException {
        User user = resourceClass.getUserWithEmail(getAuthenticatedUser());
        double walletBalance = user.getWallet().getWalletBalance();
        double transferAmount = transferDto.getAmount();
        double transactionLimit = resourceClass.getTransactionLimit(user);
        double transactionAmount = transactionServices.checkTransactionsPerDay(user) + transferAmount;
        if (walletBalance >= transferAmount && transactionAmount <= transactionLimit) {
            User beneficiary = userRepository.findUserByEmailOrAccountNumber(transferDto.getBeneficiaryEmailOrAccountNumber())
                    .orElseThrow(()-> new UserWithEmailNotFound(USER_NOT_FOUND));
            TransactionDto transactionDto = creditBeneficiary(transferDto, user, beneficiary);
            transactionServices.saveNewTransaction(transactionDto);
        } else if (walletBalance < transferAmount)
            throw new InsufficientResourcesException("Insufficient balance");
        else throw new TransactionLimitException("Transaction limit reached");
    }

    private TransactionDto creditBeneficiary(TransferDto transferDto, User user, User beneficiary) throws InvalidAmountException, InvalidTransactionException {
        boolean flag = walletServices.topUpWallet(transferDto.getAmount(), beneficiary);
        TransactionDto transactionDto;
        double transferAmount = transferDto.getAmount();
        if (flag) {
            double walletBalance = user.getWallet().getWalletBalance();
            walletBalance = walletBalance - transferAmount;
            user.getWallet().setWalletBalance(walletBalance);
            userRepository.save(user);
            transactionDto = new TransactionDto(user, transferAmount, TRANSFER, APPROVED);
        } else
            transactionDto = new TransactionDto(user, transferAmount, TRANSFER, DECLINED);
        return transactionDto;
    }

    private void sendVerificationMail(User user) throws MessagingException {
        UserDetails userDetails = new MyUserDetails(user);
        String token = jwtTokenUtil.generateToken(userDetails);
        String content = "Thank you for signing up to the platform, " +
                "kindly click on the link below to activate your account : \n" +
                Constants.BASE_URL + "api/auth/account-verification/" + token;
        MailDto mailDto = new MailDto();
        mailDto.setBody(content);
        mailDto.setSubject("Account Verification");
        mailDto.setTo(user.getEmail());
        mailService.sendMail(mailDto);
    }
}
