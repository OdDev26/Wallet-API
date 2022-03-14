package com.odcode.Wallet.API.transaction_payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountNoTransferPayLoad {
    private Long fromAccountNo;
    private Long toAccountNo;
    private BigDecimal amount;
}
