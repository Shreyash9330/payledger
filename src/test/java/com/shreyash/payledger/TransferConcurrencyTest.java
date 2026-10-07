package com.shreyash.payledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.shreyash.payledger.dto.RegisterRequest;
import com.shreyash.payledger.repository.WalletRepository;
import com.shreyash.payledger.service.AuthService;
import com.shreyash.payledger.service.PaymentService;
import com.shreyash.payledger.service.TransferFacade;

@SpringBootTest
class TransferConcurrencyTest {
    @Autowired AuthService authService;
    @Autowired PaymentService paymentService;
    @Autowired TransferFacade transferFacade;
    @Autowired WalletRepository walletRepository;

    @Test
    void concurrentTransfersKeepBalancesConsistent() throws Exception {
        String a = "a-" + UUID.randomUUID() + "@test.com";
        String b = "b-" + UUID.randomUUID() + "@test.com";
        authService.register(new RegisterRequest("A", a, "Test@1234"));
        authService.register(new RegisterRequest("B", b, "Test@1234"));
        paymentService.deposit(a, "dep-" + UUID.randomUUID(), new BigDecimal("1000"));

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    transferFacade.transfer(a, b, "trf-" + UUID.randomUUID(), new BigDecimal("10"));
                    success.incrementAndGet();
                } catch (Exception ignored) { }
                return null;
            }));
        }
        ready.await();
        start.countDown();          // sab threads ek saath chhoot jayein
        for (Future<?> f : futures) f.get();
        pool.shutdown();

        BigDecimal balA = walletRepository.findByUserEmail(a).get().getBalance();
        BigDecimal balB = walletRepository.findByUserEmail(b).get().getBalance();
        BigDecimal moved = new BigDecimal("10").multiply(BigDecimal.valueOf(success.get()));
        System.out.println("Successful transfers: " + success.get() + " / " + threads);

        assertEquals(0, new BigDecimal("1000").subtract(moved).compareTo(balA));
        assertEquals(0, moved.compareTo(balB));
        assertEquals(0, new BigDecimal("1000").compareTo(balA.add(balB)));
    }
    
}
