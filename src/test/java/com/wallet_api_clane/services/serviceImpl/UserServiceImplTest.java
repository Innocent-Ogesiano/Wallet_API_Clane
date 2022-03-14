package com.wallet_api_clane.services.serviceImpl;

import com.wallet_api_clane.dtos.*;
import com.wallet_api_clane.exceptions.*;
import com.wallet_api_clane.models.Address;
import com.wallet_api_clane.models.User;
import com.wallet_api_clane.models.Wallet;
import com.wallet_api_clane.repositories.AddressRepository;
import com.wallet_api_clane.repositories.UserRepository;
import com.wallet_api_clane.services.MailService;
import com.wallet_api_clane.services.TransactionServices;
import com.wallet_api_clane.services.WalletServices;
import com.wallet_api_clane.utils.ResourceClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import javax.naming.InsufficientResourcesException;
import javax.transaction.InvalidTransactionException;

import java.util.Optional;

import static com.wallet_api_clane.enums.KYCLevel.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private WalletServices walletServices;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private ResourceClass resourceClass;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private TransactionServices transactionServices;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .email("og@gmail.com")
                .phoneNumber("12345678901")
                .password("12345678")
                .build();

    }

    @Test
    void saveNewUser() throws MessagingException {
        SignupDto signupDto = new SignupDto();
        signupDto.setPhoneNumber("12345678901");
        when(mapper.map(signupDto, User.class)).thenReturn(user);
        userService.saveNewUser(signupDto);
        assertEquals(signupDto.getPhoneNumber().substring(1), user.getAccountNumber());
        verify(userRepository, times(1)).save(user);
        verify(mailService, times(1)).sendVerificationMail(user);
        verify(walletServices, times(1)).setNewUserWallet(user);
    }
    
    @Test
    void captureValuesWhenSaveNewUserCalled () throws MessagingException {
        SignupDto signupDto = new SignupDto();
        signupDto.setPhoneNumber("12345678901");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(mapper.map(signupDto, User.class)).thenReturn(user);
        doNothing().when(walletServices).setNewUserWallet(captor.capture());
        doNothing().when(mailService).sendVerificationMail(captor.capture());
        userService.saveNewUser(signupDto);

        assertEquals(user, captor.getValue());
    }

    @Test
    void deposit() throws InvalidAmountException, InvalidTransactionException {
        mockAuthenticatedUser();

        mockUser();
        when(walletServices.topUpWallet(200.00, user)).thenReturn(true);
        userService.deposit(200.00);
        verify(walletServices, times(1)).topUpWallet(200.00, user);
    }

    @Test
    void givenInvalidAmount_shouldThrow_InvalidTractionException() throws InvalidAmountException, InvalidTransactionException {
        mockAuthenticatedUser();

        mockUser();
        when(walletServices.topUpWallet(0.0, user)).thenReturn(false);
        assertThrows(InvalidTransactionException.class, ()->
                userService.deposit(0));
    }

    @Test
    void checkBalance() {
        mockAuthenticatedUser();
        user.setWallet(new Wallet(200.00));
        mockUser();
        when(walletServices.checkWalletBalance(user)).thenReturn(user.getWallet().getWalletBalance());
        double walletBalance = userService.checkBalance();
        assertThat(walletBalance)
                .isNotNull()
                .isEqualTo(user.getWallet().getWalletBalance())
                .isEqualTo(200.00);

    }

    @Test
    void upgradeToLevel2() {
        Level2Dto level2Dto = new Level2Dto("first name", "last name", "bvn");
        mockAuthenticatedUser();
        user.setKycLevel(LEVEL_1);
        mockUser();
        userService.upgradeToLevel2(level2Dto);
        assertEquals(LEVEL_2, user.getKycLevel());
        verify(mapper, times(1)).map(level2Dto, user);
    }

    @Test
    void shouldThrow_AlreadyUpgradedException_whenUserHasAlreadyUpgradedToLevel2 () {
        Level2Dto level2Dto = new Level2Dto("first name", "last name", "bvn");
        mockAuthenticatedUser();
        user.setKycLevel(LEVEL_2);
        mockUser();
        assertThrows(AlreadyUpgradedException.class, ()->
                userService.upgradeToLevel2(level2Dto));
    }

    @Test
    void upgradeToLevel3() {
        Level3Dto level3Dto = getLevel3Dto();
        user.setKycLevel(LEVEL_2);
        mockUser();
        when(mapper.map(level3Dto, Address.class)).thenReturn(new Address());
        userService.upgradeToLevel3(level3Dto);
        assertEquals(LEVEL_3, user.getKycLevel());
    }

    @Test
    void shouldThrow_AlreadyUpgradedException_whenUserHasAlreadyUpgradedToLevel3() {
        Level3Dto level3Dto = getLevel3Dto();
        user.setKycLevel(LEVEL_3);
        mockUser();
        assertThrows(AlreadyUpgradedException.class, ()->
                userService.upgradeToLevel3(level3Dto));
    }

    @Test
    void shouldThrow_NotQualifiedException_whenUserHasNotUpgradedToLevel2 () {
        Level3Dto level3Dto = getLevel3Dto();
        user.setKycLevel(LEVEL_1);
        mockUser();
        assertThrows(NotQualifiedException.class, ()->
                userService.upgradeToLevel3(level3Dto));
    }

    private Level3Dto getLevel3Dto() {
        AddressDto addressDto = new AddressDto();
        Level3Dto level3Dto = Level3Dto.builder()
                .address(addressDto)
                .nin("nin")
                .build();
        mockAuthenticatedUser();
        return level3Dto;
    }

    @Test
    void transferMoneyToAnotherUser() throws InvalidAmountException, InvalidTransactionException, InsufficientResourcesException {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        User beneficiary = User.builder()
                .email("beneficiary")
                .accountNumber("account number")
                .build();
        TransferDto transferDto = new TransferDto("beneficiary", 30000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        when(userRepository.findUserByEmailOrAccountNumber(transferDto.getBeneficiaryEmailOrAccountNumber()))
                .thenReturn(Optional.of(beneficiary));
        when(walletServices.topUpWallet(transferDto.getAmount(), beneficiary)).thenReturn(true);

        userService.transferMoneyToAnotherUser(transferDto);
        assertEquals(20000.00, user.getWallet().getWalletBalance());
    }

    @Test
    void shouldThrow_InsufficientResourcesException_whenUserWalletBalanceIsLessThanTransferAmount () {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        TransferDto transferDto = new TransferDto("beneficiary", 60000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        assertThrows(InsufficientResourcesException.class, ()->
                userService.transferMoneyToAnotherUser(transferDto));
    }

    @Test
    void shouldThrow_TransactionLimitException_whenUserExceedsDailyTransactionLimit () {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        TransferDto transferDto = new TransferDto("beneficiary", 20000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(40000.00);
        assertThrows(TransactionLimitException.class, ()->
                userService.transferMoneyToAnotherUser(transferDto));
    }

    @Test
    void shouldThrow_InvalidAmountException_whenTransferAmountIsNotValid () {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        TransferDto transferDto = new TransferDto("beneficiary", 0.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(40000.00);
        assertThrows(InvalidAmountException.class, ()->
                userService.transferMoneyToAnotherUser(transferDto));
    }

    @Test
    void shouldThrow_UserWithEmailNotFoundException_whenBeneficiaryIsNotFound () {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        TransferDto transferDto = new TransferDto("beneficiary", 30000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        when(userRepository.findUserByEmailOrAccountNumber(transferDto.getBeneficiaryEmailOrAccountNumber()))
                .thenReturn(Optional.empty());
        assertThrows(UserWithEmailNotFound.class, ()->
                userService.transferMoneyToAnotherUser(transferDto));
    }

    private void setUserWallet() {
        user.setWallet(new Wallet(50000.00));
    }

    @Test
    void shouldThrow_TransactionDeclinedException_whenTransactionIsDeclined () throws InvalidAmountException, InvalidTransactionException {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        User beneficiary = User.builder()
                .email("beneficiary")
                .accountNumber("account number")
                .build();
        TransferDto transferDto = new TransferDto("beneficiary", 30000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        when(userRepository.findUserByEmailOrAccountNumber(transferDto.getBeneficiaryEmailOrAccountNumber()))
                .thenReturn(Optional.of(beneficiary));
        when(walletServices.topUpWallet(transferDto.getAmount(), beneficiary)).thenReturn(false);
        assertThrows(TransactionDeclinedException.class, ()->
                userService.transferMoneyToAnotherUser(transferDto));
    }

    @Test
    void withdrawFromWallet() throws InsufficientResourcesException, InvalidAmountException {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        WithdrawalDto withdrawalDto = new WithdrawalDto(30000.00);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        doNothing().when(walletServices).withdrawFromWallet(any(Double.class), captor.capture());
        userService.withdrawFromWallet(withdrawalDto);
        verify(walletServices, times(1))
                .withdrawFromWallet(withdrawalDto.getWithdrawalAmount(), user);
        assertEquals(user, captor.getValue());
    }

    @Test
    void shouldThrow_InvalidAmountException_whenWithdrawalAmountIsNotValid () {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        WithdrawalDto withdrawalDto = new WithdrawalDto(0.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        assertThrows(InvalidAmountException.class, ()->
                userService.withdrawFromWallet(withdrawalDto));
    }

    @Test
    void shouldThrow_TransactionLimitException_whenUserExceedsDailyTransactionLimitForWithdrawal () {
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        WithdrawalDto withdrawalDto = new WithdrawalDto(10000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(50000.00);
        assertThrows(TransactionLimitException.class, ()->
                userService.withdrawFromWallet(withdrawalDto));
    }

    @Test
    void shouldThrow_InsufficientResourcesException_whenUserWalletBalanceIsLessThanWithdrawalAmount (){
        setUserWallet();
        mockAuthenticatedUser();
        mockUser();
        WithdrawalDto withdrawalDto = new WithdrawalDto(100000.00);
        when(resourceClass.getTransactionLimit(user)).thenReturn(50000.00);
        when(transactionServices.checkTransactionsForTheDay(user)).thenReturn(0.00);
        assertThrows(InsufficientResourcesException.class, ()->
                userService.withdrawFromWallet(withdrawalDto));
    }

    public static void mockAuthenticatedUser() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        UserDetails userDetails = withUsername("og@gmail.com").password("password").roles("USER").build();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
        when((UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(userDetails);
    }

    private void mockUser() {
        when(resourceClass.getUserWithEmail("og@gmail.com")).thenReturn(user);
    }

}