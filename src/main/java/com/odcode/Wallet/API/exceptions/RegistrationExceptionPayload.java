package com.odcode.Wallet.API.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationExceptionPayload {
    private String message;
}
