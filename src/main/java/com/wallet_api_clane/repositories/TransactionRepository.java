package com.wallet_api_clane.repositories;

import com.wallet_api_clane.enums.TransactionStatus;
import com.wallet_api_clane.models.Transaction;
import com.wallet_api_clane.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsByUserAndCreatedAtIsAfterAndStatus(User user, LocalDateTime localDateTime, TransactionStatus status);
}
