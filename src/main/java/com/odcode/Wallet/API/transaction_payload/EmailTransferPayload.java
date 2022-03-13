package com.odcode.Wallet.API.transaction_payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTransferPayload {
    private String fromEmail;
    private String toEmail;
    private BigDecimal amount;
}
