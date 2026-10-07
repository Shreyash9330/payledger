package com.shreyash.payledger.service;

import java.math.BigDecimal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.shreyash.payledger.dto.TransactionResponse;

@Service
public class TransferFacade {
    private static final int MAX_RETRIES = 5;
    private final PaymentService paymentService;

    public TransferFacade(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // @Transactional yahan NAHI lagana, har retry ek nayi transaction honi chahiye
    public TransactionResponse transfer(String from, String to, String key, BigDecimal amount) {
        for (int attempt = 1; ; attempt++) {
            try {
                return paymentService.transfer(from, to, key, amount);
            } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException e) {
                if (attempt >= MAX_RETRIES) throw e;
            }
        }
    }
}