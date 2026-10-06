package com.shreyash.payledger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shreyash.payledger.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserEmail(String email);
}