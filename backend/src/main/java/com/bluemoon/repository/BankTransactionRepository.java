package com.bluemoon.repository;

import com.bluemoon.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    Optional<BankTransaction> findByExternalTransactionId(String externalTransactionId);
    boolean existsByExternalTransactionId(String externalTransactionId);
}
