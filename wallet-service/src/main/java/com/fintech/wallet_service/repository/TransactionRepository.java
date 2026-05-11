package com.fintech.wallet_service.repository;

import com.fintech.wallet_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletEmailOrderByTimestampDesc(String email);
}
