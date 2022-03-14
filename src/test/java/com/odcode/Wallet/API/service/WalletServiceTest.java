package com.odcode.Wallet.API.service;

import com.odcode.Wallet.API.exceptions.TransactionFailedException;
import com.odcode.Wallet.API.model.KycLevel;
import com.odcode.Wallet.API.model.Wallet;
import com.odcode.Wallet.API.registration_request.WalletRegistrationRequest;
import com.odcode.Wallet.API.repository.WalletRepository;
import com.odcode.Wallet.API.transaction_payload.AccountNoTransferPayLoad;
import com.odcode.Wallet.API.transaction_payload.EmailTransferPayload;
import com.odcode.Wallet.API.transaction_payload.WalletIdTransferPayLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    private WalletService walletService;


    @BeforeEach
    void setUp() {
        walletService= new WalletService(walletRepository);

    }

    @Test
    void registerUser() {
        WalletRegistrationRequest request1= new WalletRegistrationRequest(
         "Mike","Egbe","Male","od@gmail.com", LocalDate.of(1990,03,21), KycLevel.LEVEL3);

        WalletRegistrationRequest request2= new WalletRegistrationRequest(
                "Mike","Egbe","Male","od@gmail.com", LocalDate.of(1991,03,21), KycLevel.LEVEL2);

        WalletRegistrationRequest request3= new WalletRegistrationRequest(
                "Mike","Egbe","Male","od@gmail.com", LocalDate.of(1990,03,21), KycLevel.LEVEL1);


        Long accountNo = walletService.registerUser(request1);
        walletService.registerUser(request2);
        walletService.registerUser(request3);
        assertNotNull(accountNo);

    }


    @Test
    void deposit() {
        Wallet wallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
        ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));
        Mockito.when(walletRepository.findWalletByAccountNo(67676767676L)).thenReturn(wallet);
        BigDecimal amount= BigDecimal.valueOf(100000);
        BigDecimal balance = walletService.deposit(amount, 67676767676L);
        assertNotNull(balance);
    }

    @Test
    void transferFundsViaAccountNumber() {
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(100000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        BigDecimal receiverBalance= receiverWallet.getBalance();

        Mockito.when(walletRepository.findWalletByAccountNo(67676767676L)).thenReturn(senderWallet);
        Mockito.when(walletRepository.findWalletByAccountNo(63676767679L)).thenReturn(receiverWallet);

        AccountNoTransferPayLoad accountNoTransferPayLoad= new AccountNoTransferPayLoad(senderWallet.getAccountNo(),receiverWallet.getAccountNo(),BigDecimal.valueOf(50000));
        walletService.transferFundsViaAccountNumber(accountNoTransferPayLoad);
        assertEquals(receiverBalance.add(accountNoTransferPayLoad.getAmount()),receiverWallet.getBalance());
    }

    @Test
    void throwsInsufficientBalance(){
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByAccountNo(67676767676L)).thenReturn(senderWallet);


        AccountNoTransferPayLoad accountNoTransferPayLoad= new AccountNoTransferPayLoad(senderWallet.getAccountNo(),receiverWallet.getAccountNo(),BigDecimal.valueOf(50000));


        assertThatThrownBy(()-> walletService.transferFundsViaAccountNumber(accountNoTransferPayLoad))
                .hasMessageContaining("Insufficient Balance")
                .isInstanceOf(TransactionFailedException.class);


    }
    @Test
    void throwsMaximumDailyTransferLimitExceeded(){
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(1000000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByAccountNo(67676767676L)).thenReturn(senderWallet);


        AccountNoTransferPayLoad accountNoTransferPayLoad= new AccountNoTransferPayLoad(senderWallet.getAccountNo(),receiverWallet.getAccountNo(),BigDecimal.valueOf(1000000));


        assertThatThrownBy(()-> walletService.transferFundsViaAccountNumber(accountNoTransferPayLoad))
                .hasMessageContaining("Maximum Daily Transfer exceeded")
                .isInstanceOf(TransactionFailedException.class);


    }
    @Test
    void transferFundsViaEmail() {
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(100000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        BigDecimal receiverBalance= receiverWallet.getBalance();

        Mockito.when(walletRepository.findWalletByEmail("mike@gmail.com")).thenReturn(senderWallet);
        Mockito.when(walletRepository.findWalletByEmail("od@gmail.com")).thenReturn(receiverWallet);

        EmailTransferPayload emailTransferPayload = new EmailTransferPayload(senderWallet.getEmail(),receiverWallet.getEmail(),BigDecimal.valueOf(50000));
        walletService.transferFundsViaEmail(emailTransferPayload);
        assertEquals(receiverBalance.add(emailTransferPayload.getAmount()),receiverWallet.getBalance());

    }
    @Test
    void throwsInsufficientBalanceForEmailTransfer(){
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByEmail("mike@gmail.com")).thenReturn(senderWallet);

        EmailTransferPayload emailTransferPayload= new EmailTransferPayload(senderWallet.getEmail(),receiverWallet.getEmail(),BigDecimal.valueOf(50000));


        assertThatThrownBy(()-> walletService.transferFundsViaEmail(emailTransferPayload))
                .hasMessageContaining("Insufficient Balance")
                .isInstanceOf(TransactionFailedException.class);


    }
    @Test
    void throwsMaximumDailyTransferLimitExceededForEmailTransfer(){
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(1000000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByEmail("mike@gmail.com")).thenReturn(senderWallet);


        EmailTransferPayload emailTransferPayload= new EmailTransferPayload(senderWallet.getEmail(),receiverWallet.getEmail(),BigDecimal.valueOf(1000000));


        assertThatThrownBy(()-> walletService.transferFundsViaEmail(emailTransferPayload))
                .hasMessageContaining("Maximum Daily Transfer exceeded")
                .isInstanceOf(TransactionFailedException.class);


    }



    @Test
    void transferFundsViaViaWalletIdTransfer() {
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(100000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        BigDecimal receiverBalance= receiverWallet.getBalance();

        Mockito.when(walletRepository.findWalletByWalletId(1)).thenReturn(senderWallet);
        Mockito.when(walletRepository.findWalletByWalletId(2)).thenReturn(receiverWallet);

        WalletIdTransferPayLoad walletIdTransferPayLoad= new WalletIdTransferPayLoad(senderWallet.getWalletId(),receiverWallet.getWalletId(),BigDecimal.valueOf(50000));
        walletService.transferViaWalletId(walletIdTransferPayLoad);
        assertEquals(receiverBalance.add(walletIdTransferPayLoad.getAmount()),receiverWallet.getBalance());
    }

    @Test
    void throwsInsufficientBalanceViaWalletIdTransfer(){
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByWalletId(1)).thenReturn(senderWallet);


        WalletIdTransferPayLoad walletIdTransferPayLoad= new WalletIdTransferPayLoad(senderWallet.getWalletId(),receiverWallet.getWalletId(),BigDecimal.valueOf(500000));

        assertThatThrownBy(()-> walletService.transferViaWalletId(walletIdTransferPayLoad))
                .hasMessageContaining("Insufficient Balance")
                .isInstanceOf(TransactionFailedException.class);


    }
    @Test
    void throwsMaximumDailyTransferLimitExceededViaWalletIdTransfer(){
        Wallet senderWallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(1000000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Wallet receiverWallet=new Wallet(2,"Od","Egbe","Male",63676767679L,"od@gmail.com",LocalDate.of(1981,02,21),KycLevel.LEVEL2
                ,BigDecimal.valueOf(0.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByWalletId(1)).thenReturn(senderWallet);

        WalletIdTransferPayLoad walletIdTransferPayLoad= new WalletIdTransferPayLoad(senderWallet.getWalletId(),receiverWallet.getWalletId(),BigDecimal.valueOf(1000000));



        assertThatThrownBy(()-> walletService.transferViaWalletId(walletIdTransferPayLoad))
                .hasMessageContaining("Maximum Daily Transfer exceeded")
                .isInstanceOf(TransactionFailedException.class);


    }



    @Test
    void withdrawAmount() {
        Wallet wallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(500000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        BigDecimal walletBalance= wallet.getBalance();
        Mockito.when(walletRepository.findWalletByAccountNo(wallet.getAccountNo())).thenReturn(wallet);
        BigDecimal balance = walletService.withdrawAmount(wallet.getAccountNo(), BigDecimal.valueOf(50000));

        assertEquals(walletBalance.subtract(BigDecimal.valueOf(50000)),balance);

    }
    @Test
    void throwsInsufficientBalanceOnWithdrawal() {
        Wallet wallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(500000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByAccountNo(wallet.getAccountNo())).thenReturn(wallet);

        assertThatThrownBy(()->walletService.withdrawAmount(wallet.getAccountNo(), BigDecimal.valueOf(1000000)))
                .hasMessageContaining("Insufficient balance")
                .isInstanceOf(TransactionFailedException.class);

    }
    @Test
    void throwsWithdrawalLimitExceeded() {
        Wallet wallet= new Wallet(1,"Mike","Egbe","Male",67676767676L,"mike@gmail.com",LocalDate.of(1980,02,21),KycLevel.LEVEL3
                ,BigDecimal.valueOf(1000000.00),BigDecimal.valueOf(500000),BigDecimal.valueOf(500000));

        Mockito.when(walletRepository.findWalletByAccountNo(wallet.getAccountNo())).thenReturn(wallet);
        assertThatThrownBy(()->walletService.withdrawAmount(wallet.getAccountNo(), BigDecimal.valueOf(1000000)))
                .hasMessageContaining("Withdrawal limit exceeded")
                .isInstanceOf(TransactionFailedException.class);

    }

}