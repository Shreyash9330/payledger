package com.shreyash.payledger.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.shreyash.payledger.enums.TransactionStatus;
import com.shreyash.payledger.enums.TransactionType;

public record TransactionResponse(
	    Long id, TransactionType type, TransactionStatus status, BigDecimal amount, Instant createdAt) {}
