package com.odcode.Wallet.API.transaction_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
public class TransactionStatus {
    private String message;
    private HttpStatus status;
}
