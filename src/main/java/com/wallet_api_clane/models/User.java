package com.wallet_api_clane.models;

import com.wallet_api_clane.enums.KYCLevel;
import com.wallet_api_clane.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Getter
@Setter
@Entity
@Table(name = "user_table")
public class User extends BaseModel {
    @Column(nullable = false, unique = true)
    @Email(message = "Must be a valid email address")
    private String email;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false)
    private String password;
    private boolean isAccountVerified;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private KYCLevel kycLevel = KYCLevel.LEVEL_1;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @OneToOne
    private Wallet wallet;
}
