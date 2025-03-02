package com.example.freelance_java_puppet.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {
    private Long id;
    private double amount;
    private String currency;
}
