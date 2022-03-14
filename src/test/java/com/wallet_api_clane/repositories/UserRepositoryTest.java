package com.wallet_api_clane.repositories;

import com.wallet_api_clane.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User user;

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .password("password")
                .isAccountVerified(true)
                .email("email@email")
                .firstName("first name")
                .lastName("last name")
                .phoneNumber("phonenumber")
                .accountNumber("accountNumber")
                .build();
    }

    @Test
    void saveUser() {
        User SavedUser = userRepository.save(user);
        assertThat(SavedUser)
                .isNotNull()
                .isEqualTo(user);
    }

    @Test
    void findUserByEmail() {
        userRepository.save(user);
        Optional<User> returnedUser = userRepository.findUserByEmail(user.getEmail());
        assertThat(returnedUser).isPresent();
        assertThat(returnedUser.get())
                .isNotNull()
                .isEqualTo(user);
    }

    @Test
    void findUserByEmailOrPhoneNumber() {
        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findUserByEmailOrPhoneNumber("email@email", "");
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get()).isEqualTo(user);
    }

    @Test
    void findUserByEmailOrAccountNumber() {
        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findUserByEmailOrAccountNumber("email@email");
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get())
                .isNotNull()
                .isEqualTo(user);
    }
}