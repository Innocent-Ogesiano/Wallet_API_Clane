package com.wallet_api_clane.repositories;

import com.wallet_api_clane.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByEmailOrPhoneNumber(String email, String phoneNumber);
}
