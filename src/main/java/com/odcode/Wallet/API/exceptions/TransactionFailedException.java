package com.odcode.Wallet.API.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String message) {
        super(message);
    }
}
