package com.shreyash.payledger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shreyash.payledger.entity.LedgerEntry;
import com.shreyash.payledger.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
