package com.example.freelance_java_puppet.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private int id;
    private String firstName;
    private String lastName;
    private List<TransactionDTO> transactions;
}
