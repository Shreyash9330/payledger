package com.shreyash.payledger.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shreyash.payledger.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    
    @Query("""
    	    select t from Transaction t
    	    left join t.fromWallet fw left join fw.user fu
    	    left join t.toWallet tw left join tw.user tu
    	    where fu.email = :email or tu.email = :email
    	    order by t.createdAt desc
    	    """)
    	Page<Transaction> findHistory(@Param("email") String email, Pageable pageable);
}
