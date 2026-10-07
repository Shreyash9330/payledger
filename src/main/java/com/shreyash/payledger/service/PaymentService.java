package com.shreyash.payledger.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.shreyash.payledger.dto.PageResponse;
import com.shreyash.payledger.dto.TransactionResponse;
import com.shreyash.payledger.entity.LedgerEntry;
import com.shreyash.payledger.entity.Transaction;
import com.shreyash.payledger.entity.Wallet;
import com.shreyash.payledger.enums.EntryType;
import com.shreyash.payledger.enums.TransactionStatus;
import com.shreyash.payledger.enums.TransactionType;
import com.shreyash.payledger.repository.LedgerEntryRepository;
import com.shreyash.payledger.repository.TransactionRepository;
import com.shreyash.payledger.repository.WalletRepository;

@Service
public class PaymentService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public PaymentService(WalletRepository walletRepository,
                          TransactionRepository transactionRepository,
                          LedgerEntryRepository ledgerEntryRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public TransactionResponse deposit(String email, String key, BigDecimal amount) {
        var existing = transactionRepository.findByIdempotencyKey(key);
        if (existing.isPresent()) return toResponse(existing.get());   // duplicate request

        Wallet wallet = getWallet(email);

        Transaction tx = new Transaction();
        tx.setIdempotencyKey(key);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setToWallet(wallet);
        tx.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(tx);

        wallet.setBalance(wallet.getBalance().add(amount));
        recordLedger(tx, wallet, EntryType.CREDIT, amount);
        return toResponse(tx);
    }

    @Transactional
    public TransactionResponse transfer(String fromEmail, String toEmail, String key, BigDecimal amount) {
        var existing = transactionRepository.findByIdempotencyKey(key);
        if (existing.isPresent()) return toResponse(existing.get());

        if (fromEmail.equalsIgnoreCase(toEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer to yourself");
        }
        String first = fromEmail.compareTo(toEmail) < 0 ? fromEmail : toEmail;
        String second = first.equals(fromEmail) ? toEmail : fromEmail;
        Wallet w1 = getWalletForUpdate(first);
        Wallet w2 = getWalletForUpdate(second);
        Wallet from = first.equals(fromEmail) ? w1 : w2;
        Wallet to = first.equals(fromEmail) ? w2 : w1;

        if (from.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        Transaction tx = new Transaction();
        tx.setIdempotencyKey(key);
        tx.setType(TransactionType.TRANSFER);
        tx.setAmount(amount);
        tx.setFromWallet(from);
        tx.setToWallet(to);
        tx.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(tx);

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        recordLedger(tx, from, EntryType.DEBIT, amount);
        recordLedger(tx, to, EntryType.CREDIT, amount);
        return toResponse(tx);
    }

    private Wallet getWallet(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for " + email));
    }

    private void recordLedger(Transaction tx, Wallet wallet, EntryType type, BigDecimal amount) {
        LedgerEntry e = new LedgerEntry();
        e.setTransaction(tx);
        e.setWallet(wallet);
        e.setEntryType(type);
        e.setAmount(amount);
        e.setBalanceAfter(wallet.getBalance());
        ledgerEntryRepository.save(e);
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(t.getId(), t.getType(), t.getStatus(), t.getAmount(), t.getCreatedAt());
    }
    
    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> history(String email, int page, int size) {
        Page<Transaction> result = transactionRepository.findHistory(email, PageRequest.of(page, Math.min(size, 50)));
        return new PageResponse<>(result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
    
    private Wallet getWalletForUpdate(String email) {
        return walletRepository.findByUserEmailForUpdate(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for " + email));
    }
}