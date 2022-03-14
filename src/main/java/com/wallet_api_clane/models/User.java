package com.wallet_api_clane.models;

import com.wallet_api_clane.enums.KYCLevel;
import com.wallet_api_clane.enums.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private boolean isAccountVerified = false;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    private String bvn;
    private String nin;
    @OneToOne
    private Address address;
    @Enumerated(EnumType.STRING)
    private KYCLevel kycLevel = KYCLevel.LEVEL_1;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @OneToOne
    private Wallet wallet;
}
