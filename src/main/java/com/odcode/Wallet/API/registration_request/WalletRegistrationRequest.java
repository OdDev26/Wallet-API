package com.odcode.Wallet.API.registration_request;

import com.odcode.Wallet.API.model.KycLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRegistrationRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private LocalDate dateOfBirth;
    private KycLevel kycLevel;
}
