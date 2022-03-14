package com.odcode.Wallet.API.controller;

import com.odcode.Wallet.API.exceptions.TransactionFailedException;
import com.odcode.Wallet.API.transaction_payload.AccountNoTransferPayLoad;
import com.odcode.Wallet.API.transaction_payload.EmailTransferPayload;
import com.odcode.Wallet.API.transaction_payload.WalletIdTransferPayLoad;
import com.odcode.Wallet.API.registration_request.WalletRegistrationRequest;
import com.odcode.Wallet.API.service.WalletService;
import com.odcode.Wallet.API.transaction_response.RegistrationStatus;
import com.odcode.Wallet.API.transaction_response.TransactionStatus;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class WalletController {
    private WalletService walletService;

    @PostMapping("/create/wallet")
    @ApiOperation("Creates a new Wallet")
    public ResponseEntity<RegistrationStatus> createWallet(@RequestBody WalletRegistrationRequest walletRegistrationRequest){

        walletService.registerUser(walletRegistrationRequest);
        RegistrationStatus registrationStatus= new RegistrationStatus();
        if(HttpStatus.OK.is2xxSuccessful()){
            registrationStatus.setMessage("Registration successful");
            registrationStatus.setHttpStatus(HttpStatus.OK);
            return new ResponseEntity<>(registrationStatus, HttpStatus.OK);
        }
        registrationStatus.setMessage("Transaction unsuccessful");
        registrationStatus.setHttpStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(registrationStatus,HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/deposit/{accountnumber}/{amount}")
    @ApiOperation("To make deposit to wallet")
    public ResponseEntity<TransactionStatus> deposit(@PathVariable (value = "amount") BigDecimal amount, @PathVariable (value = "accountnumber") Long accountNumber){
        walletService.deposit(amount,accountNumber);

        if(HttpStatus.OK.is2xxSuccessful()){
            TransactionStatus transactionStatus= new TransactionStatus();
            transactionStatus.setMessage("Deposit Successful");
            transactionStatus.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        }
        throw new TransactionFailedException();
    }

    @PutMapping("/withdraw/{accountnumber}/{amount}")
    @ApiOperation("To withdraw from account ")
    public ResponseEntity<TransactionStatus>withdrawFromAccount(@PathVariable(value = "accountnumber") Long accountNumber,@PathVariable BigDecimal amount){
        walletService.withdrawAmount(accountNumber,amount);


        if(HttpStatus.OK.is2xxSuccessful()){
            TransactionStatus transactionStatus= new TransactionStatus();
            transactionStatus.setMessage("Withdrawal successful");
            transactionStatus.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(transactionStatus,HttpStatus.OK);
        }
        throw new TransactionFailedException();
    }

    @PostMapping("/transfer/via/walletid")
    @ApiOperation("To make transfer via walletId")
    public ResponseEntity<TransactionStatus> transferViaWalletId(@RequestBody WalletIdTransferPayLoad walletIdTransferPayLoad){
        walletService.transferViaWalletId(walletIdTransferPayLoad);

        if(HttpStatus.OK.is2xxSuccessful()){
            TransactionStatus transactionStatus= new TransactionStatus();
            transactionStatus.setMessage("Transfer successful");
            transactionStatus.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        }
        throw new TransactionFailedException();
    }
    @PostMapping("/transfer/via/email")
    @ApiOperation("To make transfer via email")
    public ResponseEntity<TransactionStatus> transferViaEmail(@RequestBody EmailTransferPayload emailTransferPayload){
        walletService.transferFundsViaEmail(emailTransferPayload);

        if(HttpStatus.OK.is2xxSuccessful()){
            TransactionStatus transactionStatus= new TransactionStatus();
            transactionStatus.setMessage("Transfer successful");
            transactionStatus.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        }
        throw new TransactionFailedException();
    }

    @PostMapping("/transfer/via/accountnumber")
    @ApiOperation("To make transfer via account number")
    public ResponseEntity<TransactionStatus> transferViaEmail(@RequestBody AccountNoTransferPayLoad accountNoTransferPayLoad){
        walletService.transferFundsViaAccountNumber(accountNoTransferPayLoad);

        if(HttpStatus.OK.is2xxSuccessful()){
            TransactionStatus transactionStatus= new TransactionStatus();
            transactionStatus.setMessage("Transfer successful");
            transactionStatus.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        }
        throw new TransactionFailedException();
    }
}

