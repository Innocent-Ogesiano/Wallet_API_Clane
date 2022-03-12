package com.wallet_api_clane.repositories;

import com.wallet_api_clane.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
