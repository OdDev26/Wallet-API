package com.odcode.Wallet.API.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(String message) {
        super(message);
    }
}
