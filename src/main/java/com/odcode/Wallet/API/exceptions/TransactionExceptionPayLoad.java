package com.odcode.Wallet.API.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
public class TransactionExceptionPayLoad {
    private String message;
}
