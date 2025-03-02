package com.example.freelance_java_puppet.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private double amount;

    private String currency;

    private LocalDateTime transDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime();

    private String paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne
    private User user;

}
