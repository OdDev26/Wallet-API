package com.odcode.Wallet.API.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer walletId;
    private String firstName;
    private String lastName;
    private String gender;
    @Column(unique = true)
    private Long accountNo;
    @Column(unique = true)
    private String email;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private KycLevel kycLevel;
    private BigDecimal balance;
    private BigDecimal maximumDailyWithdrawal;
    private BigDecimal maximumDailyTransfer;

}
