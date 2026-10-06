package com.shreyash.payledger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shreyash.payledger.dto.DepositRequest;
import com.shreyash.payledger.dto.TransactionResponse;
import com.shreyash.payledger.dto.TransferRequest;
import com.shreyash.payledger.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @PostMapping("/wallet/deposit")
    public TransactionResponse deposit(Authentication auth,
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody DepositRequest req) {
        return paymentService.deposit(auth.getName(), key, req.amount());
    }

    @PostMapping("/transfers")
    public TransactionResponse transfer(Authentication auth,
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody TransferRequest req) {
        return paymentService.transfer(auth.getName(), req.toEmail(), key, req.amount());
    }
}