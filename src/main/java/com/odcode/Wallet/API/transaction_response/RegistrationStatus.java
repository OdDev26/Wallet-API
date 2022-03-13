package com.odcode.Wallet.API.transaction_response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
public class RegistrationStatus {
    private String message;
    private HttpStatus httpStatus;
}
