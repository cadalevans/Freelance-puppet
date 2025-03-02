package com.example.freelance_java_puppet.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String email;

    private String password;

    private boolean isVerified;

    private String code;

    private LocalDateTime codeExpiryDate;

    @JsonIgnore  // This will prevent the 'card' field from being serialized
    @OneToOne
    private Card card;

    @JsonManagedReference // Serializes 'histories' field only
    @ManyToMany
    private List<History> histories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions = new ArrayList<>();

}
