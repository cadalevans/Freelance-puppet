package com.example.freelance_java_puppet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double totalPrice;

    @JsonIgnore // Prevents serialization of 'user' field
    @OneToOne(mappedBy = "card")
    private User user;

    @ManyToMany
    private List<History> items = new ArrayList<>();

    public List<History> getHistories() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<History> getItems() {
        return items;
    }

    public void setItems(List<History> items) {
        this.items = items;
    }

    public User getUser() {
        return user;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
