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

    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime codeExpiryDate;

    @JsonIgnore  // This will prevent the 'card' field from being serialized
    @OneToOne
    private Card card;

    @JsonIgnore // Serializes 'histories' field only
    @ManyToMany
    private List<History> histories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions = new ArrayList<>();

    public void setCard(Card card) {
        this.card = card;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public Card getCard() {
        return card;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public LocalDateTime getCodeExpiryDate() {
        return codeExpiryDate;
    }

    public void setCodeExpiryDate(LocalDateTime codeExpiryDate) {
        this.codeExpiryDate = codeExpiryDate;
    }

    public Role getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }
}
