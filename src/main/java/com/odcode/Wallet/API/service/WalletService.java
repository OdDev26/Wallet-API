package com.odcode.Wallet.API.service;

import com.odcode.Wallet.API.exceptions.RegistrationFailedException;
import com.odcode.Wallet.API.exceptions.TransactionFailedException;
import com.odcode.Wallet.API.model.KycLevel;
import com.odcode.Wallet.API.model.Wallet;
import com.odcode.Wallet.API.transaction_payload.AccountNoTransferPayLoad;
import com.odcode.Wallet.API.transaction_payload.EmailTransferPayload;
import com.odcode.Wallet.API.transaction_payload.WalletIdTransferPayLoad;
import com.odcode.Wallet.API.registration_request.WalletRegistrationRequest;
import com.odcode.Wallet.API.repository.WalletRepository;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WalletService {
    private WalletRepository walletRepository;


    public Long registerUser(WalletRegistrationRequest walletRegistrationRequest) {
        try {
            Wallet wallet = new Wallet();
            wallet.setEmail(walletRegistrationRequest.getEmail());
            wallet.setFirstName(walletRegistrationRequest.getFirstName());
            wallet.setKycLevel(walletRegistrationRequest.getKycLevel());
            wallet.setGender(walletRegistrationRequest.getGender());
            wallet.setLastName(walletRegistrationRequest.getLastName());
            wallet.setDateOfBirth(walletRegistrationRequest.getDateOfBirth());

            Long accountNumber = generateAccountNumber(60000000000L, 69000000000L);

            wallet.setAccountNo(accountNumber);
            wallet.setBalance(BigDecimal.valueOf(0.00));
            setTransactionLimit(walletRegistrationRequest, wallet);
            walletRepository.save(wallet);
            return accountNumber;
        } catch (Exception e) {
            throwException();
        }
        return null;
    }

    public void throwException(){
        throw new RegistrationFailedException("Registration Failed, Email already exists");
    }

    private void setTransactionLimit(WalletRegistrationRequest walletRegistrationRequest, Wallet wallet) {
        KycLevel kycLevel = walletRegistrationRequest.getKycLevel();
        if (kycLevel.toString().equals("LEVEL1")) {
            wallet.setMaximumDailyWithdrawal(BigDecimal.valueOf(50000));
            wallet.setMaximumDailyTransfer(BigDecimal.valueOf(50000));
        }
        if (kycLevel.toString().equals("LEVEL2")) {
            wallet.setMaximumDailyWithdrawal(BigDecimal.valueOf(200000));
            wallet.setMaximumDailyTransfer(BigDecimal.valueOf(200000));

        }
        if (kycLevel.toString().equals("LEVEL3")) {
            wallet.setMaximumDailyWithdrawal(BigDecimal.valueOf(500000));
            wallet.setMaximumDailyTransfer(BigDecimal.valueOf(500000));

        }
    }

    public static Long generateAccountNumber(Long min, Long max) {
        return (Long) (long) (Math.random() * (max - min + 1) + min);
    }

    public BigDecimal deposit(BigDecimal amount, Long accountNumber) {
        Wallet wallet = walletRepository.findWalletByAccountNo(accountNumber);
        if (Objects.equals(wallet.getBalance(), BigDecimal.valueOf(0.00))) {
            wallet.setBalance(amount);
        }
        if (!Objects.equals(wallet.getBalance(), BigDecimal.valueOf(0.00))) {
            wallet.setBalance(wallet.getBalance().add(amount));
        }
        walletRepository.save(wallet);
        return wallet.getBalance();
    }

    public BigDecimal withdrawAmount(Long accountNumber, BigDecimal amount) {


        Wallet wallet = walletRepository.findWalletByAccountNo(accountNumber);
        BigDecimal maximumDailyWithdrawal = wallet.getMaximumDailyWithdrawal();
        BigDecimal walletBalance = wallet.getBalance();
        if (maximumDailyWithdrawal.compareTo(amount) >= 0 && walletBalance.compareTo(amount)>=0 ) {
            wallet.setBalance(wallet.getBalance().subtract(amount));
            wallet.setMaximumDailyWithdrawal(wallet.getMaximumDailyWithdrawal().subtract(amount));
            walletRepository.save(wallet);

        }
        if(walletBalance.compareTo(amount)<0){
            throw new TransactionFailedException("Insufficient balance");
        }
        if (maximumDailyWithdrawal.compareTo(amount) < 0) {
            throw new TransactionFailedException("Withdrawal limit exceeded");
        }
        return wallet.getBalance();
    }
    public void transferFundsViaAccountNumber(AccountNoTransferPayLoad accountNoTransferPayLoad1) {

        BigDecimal transferAmount = accountNoTransferPayLoad1.getAmount();
        Long senderAccountNo = accountNoTransferPayLoad1.getFromAccountNo();
        Wallet senderWallet = walletRepository.findWalletByAccountNo(senderAccountNo);
        BigDecimal senderWalletBalance = senderWallet.getBalance();
        BigDecimal senderMaximumDailyTransferLimit = senderWallet.getMaximumDailyTransfer();
        if (transferAmount.compareTo(senderMaximumDailyTransferLimit) > 0) {
            throw new TransactionFailedException("Maximum Daily Transfer exceeded");
        }


        if(transferAmount.compareTo(senderWalletBalance)>0){
            throw new TransactionFailedException("Insufficient Balance");
        }
        if (transferAmount.compareTo(senderMaximumDailyTransferLimit) <= 0 && transferAmount.compareTo(senderWalletBalance)<=0) {
            Long receiverAccountNo = accountNoTransferPayLoad1.getToAccountNo();
            Wallet receiverWallet = walletRepository.findWalletByAccountNo(receiverAccountNo);
            receiverWallet.setBalance(receiverWallet.getBalance().add(transferAmount));
            senderWallet.setBalance(senderWallet.getBalance().subtract(transferAmount));
            senderWallet.setMaximumDailyTransfer(senderMaximumDailyTransferLimit.subtract(transferAmount));
            walletRepository.save(receiverWallet);
            walletRepository.save(senderWallet);
        }

    }

    public void transferFundsViaEmail(EmailTransferPayload emailTransferPayLoad) {
        BigDecimal transferAmount = emailTransferPayLoad.getAmount();
        String senderEmail = emailTransferPayLoad.getFromEmail();
        String receiverEmail= emailTransferPayLoad.getToEmail();
        Wallet senderWallet = walletRepository.findWalletByEmail(senderEmail);
        Wallet receiverWallet= walletRepository.findWalletByEmail(receiverEmail);
        BigDecimal senderWalletBalance = senderWallet.getBalance();
        BigDecimal senderMaximumDailyTransferLimit = senderWallet.getMaximumDailyTransfer();
        if (transferAmount.compareTo(senderMaximumDailyTransferLimit) > 0) {
            throw new TransactionFailedException("Maximum Daily Transfer exceeded");
        }

        if(transferAmount.compareTo(senderWalletBalance)>0){
            throw new TransactionFailedException("Insufficient Balance");
        }
        if (transferAmount.compareTo(senderMaximumDailyTransferLimit) <= 0 && transferAmount.compareTo(senderWalletBalance)<=0) {
            senderWallet.setBalance(senderWalletBalance.subtract(transferAmount));
            receiverWallet.setBalance(receiverWallet.getBalance().add(transferAmount));
            senderWallet.setMaximumDailyTransfer(senderMaximumDailyTransferLimit.subtract(transferAmount));
            walletRepository.save(receiverWallet);
            walletRepository.save(senderWallet);
        }

    }

    public void transferViaWalletId(WalletIdTransferPayLoad walletIdTransferPayLoad){
        BigDecimal transferAmount = walletIdTransferPayLoad.getAmount();
        Integer senderWalletId= walletIdTransferPayLoad.getFromWalletId();
        Integer receiverWalletId= walletIdTransferPayLoad.getToWalletId();

        Wallet senderWallet = walletRepository.findWalletByWalletId(senderWalletId);
        Wallet receiverWallet= walletRepository.findWalletByWalletId(receiverWalletId);
        BigDecimal senderWalletBalance = senderWallet.getBalance();
        BigDecimal senderMaximumDailyTransfer = senderWallet.getMaximumDailyTransfer();

        if (transferAmount.compareTo(senderMaximumDailyTransfer) > 0) {
            throw new TransactionFailedException("Maximum Daily Transfer exceeded");
        }

        if(transferAmount.compareTo(senderWalletBalance)>0){
            throw new TransactionFailedException("Insufficient Balance");
        }
        if (transferAmount.compareTo(senderMaximumDailyTransfer) <= 0 && transferAmount.compareTo(senderWalletBalance)<=0) {
            senderWallet.setBalance(senderWalletBalance.subtract(transferAmount));
            receiverWallet.setBalance(receiverWallet.getBalance().add(transferAmount));
            senderWallet.setBalance(senderWalletBalance.subtract(transferAmount));
            senderWallet.setMaximumDailyTransfer(senderMaximumDailyTransfer.subtract(transferAmount));
            walletRepository.save(receiverWallet);
            walletRepository.save(senderWallet);
        }
    }
}
