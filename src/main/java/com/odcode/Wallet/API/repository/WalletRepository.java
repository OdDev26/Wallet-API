package com.odcode.Wallet.API.repository;

import com.odcode.Wallet.API.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
   Wallet findWalletByAccountNo(Long accountNumber);
   Wallet findWalletByEmail(String email);
   Wallet findWalletByWalletId(Integer walletId);
}
