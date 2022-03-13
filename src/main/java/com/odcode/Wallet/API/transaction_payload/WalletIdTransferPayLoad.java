package com.odcode.Wallet.API.transaction_payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletIdTransferPayLoad {
    private Integer fromWalletId;
    private Integer toWalletId;
    private BigDecimal amount;
}
