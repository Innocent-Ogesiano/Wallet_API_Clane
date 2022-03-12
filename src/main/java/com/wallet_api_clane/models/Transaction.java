package com.wallet_api_clane.models;

import com.wallet_api_clane.enums.TransactionStatus;
import com.wallet_api_clane.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction extends BaseModel {
    @ManyToOne
    private User user;
    @Column(nullable = false)
    private double amount;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;
}
