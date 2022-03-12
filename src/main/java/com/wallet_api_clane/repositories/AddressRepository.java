package com.wallet_api_clane.repositories;

import com.wallet_api_clane.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
