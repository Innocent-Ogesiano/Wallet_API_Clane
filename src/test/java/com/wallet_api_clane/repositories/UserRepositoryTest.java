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
        user = new User();
        user.setPassword("password");
        user.setAccountVerified(true);
        user.setEmail("email@email");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setPhoneNumber("phonenumber");
        user.setAccountNumber("accountNumber");
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