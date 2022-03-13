package com.odcode.Wallet.API.transaction_payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmailAndAccountNoTransferPayload {
    private Long fromAccountNo;
    private Long toAccountNo;
    private BigDecimal amount;
    private String fromEmail;
    private String toEmail;
}
