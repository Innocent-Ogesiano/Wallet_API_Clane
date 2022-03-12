package com.wallet_api_clane.repositories;

import com.wallet_api_clane.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByEmailOrPhoneNumber(String email, String phoneNumber);

    @Query(value = "SELECT * FROM USER_TABLE u WHERE u.email = :emailOrAccountNo OR u.account_number = :emailOrAccountNo", nativeQuery = true)
    Optional<User> findUserByEmailOrAccountNumber(@Param("emailOrAccountNo")String emailOrAccountNo);
}
