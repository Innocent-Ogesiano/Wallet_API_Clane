package com.wallet_api_clane.repositories;

import com.wallet_api_clane.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        User returnedUser = userRepository.findUserByEmail(user.getEmail()).get();
        assertThat(returnedUser)
                .isNotNull()
                .isEqualTo(user);
    }
}