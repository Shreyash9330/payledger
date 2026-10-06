package com.shreyash.payledger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shreyash.payledger.dto.WalletResponse;
import com.shreyash.payledger.entity.Wallet;
import com.shreyash.payledger.repository.WalletRepository;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletRepository walletRepository;

    public WalletController(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @GetMapping
    public WalletResponse myWallet(Authentication auth) {
        Wallet w = walletRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
        return new WalletResponse(w.getId(), w.getBalance(), w.getCurrency());
    }
}