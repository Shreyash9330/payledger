package com.shreyash.payledger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shreyash.payledger.dto.DepositRequest;
import com.shreyash.payledger.dto.PageResponse;
import com.shreyash.payledger.dto.TransactionResponse;
import com.shreyash.payledger.dto.TransferRequest;
import com.shreyash.payledger.service.PaymentService;
import com.shreyash.payledger.service.TransferFacade;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final TransferFacade transferFacade;

    public PaymentController(PaymentService paymentService, TransferFacade transferFacade) {
        this.paymentService = paymentService;
        this.transferFacade = transferFacade;
    }

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
        return transferFacade.transfer(auth.getName(), req.toEmail(), key, req.amount());
    }
    
    @GetMapping("/transactions")
    public PageResponse<TransactionResponse> history(Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return paymentService.history(auth.getName(), page, size);
    }
}