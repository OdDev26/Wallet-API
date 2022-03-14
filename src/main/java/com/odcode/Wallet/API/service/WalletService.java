package com.odcode.Wallet.API.service;

import com.odcode.Wallet.API.exceptions.TransactionFailedException;
import com.odcode.Wallet.API.model.KycLevel;
import com.odcode.Wallet.API.model.Wallet;
import com.odcode.Wallet.API.transaction_payload.WalletIdTransferPayLoad;
import com.odcode.Wallet.API.registration_request.WalletRegistrationRequest;
import com.odcode.Wallet.API.repository.WalletRepository;
import com.odcode.Wallet.API.transaction_payload.EmailAndAccountNoTransferPayload;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WalletService {
    private WalletRepository walletRepository;


    public Long registerUser(WalletRegistrationRequest walletRegistrationRequest) {
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
      setTransactionLimit(walletRegistrationRequest,wallet);

        walletRepository.save(wallet);
        return accountNumber;
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

    public void transferFundsViaAccountNoAndEmail(EmailAndAccountNoTransferPayload emailAndAccountNoTransferPayLoad) {
        BigDecimal transferAmount = emailAndAccountNoTransferPayLoad.getAmount();
        Long toAccountNo= emailAndAccountNoTransferPayLoad.getToAccountNo();
        String senderEmail= emailAndAccountNoTransferPayLoad.getFromEmail();
        Wallet senderWallet = walletRepository.findWalletByEmail(senderEmail);
        Wallet receiverWallet= walletRepository.findWalletByAccountNo(toAccountNo);
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
            senderWallet.setMaximumDailyTransfer(senderMaximumDailyTransfer.subtract(transferAmount));
            walletRepository.save(receiverWallet);
            walletRepository.save(senderWallet);
        }
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
