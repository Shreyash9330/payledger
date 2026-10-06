package com.shreyash.payledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shreyash.payledger.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {}