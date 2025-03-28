package com.example.freelance_java_puppet.repository;

import com.example.freelance_java_puppet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    Optional<Transaction> findByPaymentId(String paymentIntentId);
}
