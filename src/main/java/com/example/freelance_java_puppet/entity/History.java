package com.example.freelance_java_puppet.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;

    @Lob
    private String image;

    @Lob
    private String audio;

    private double price;

    @JsonBackReference // Prevents serialization of the back relationship with 'Category'
    @ManyToMany(mappedBy = "histories")
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(mappedBy = "items")
    private List<Card> cards = new ArrayList<>();

    @ManyToMany(mappedBy = "histories")
    private List<User> users = new ArrayList<>();

    public void setCard(List<Card> cards) {
        this.cards = cards;
    }
}
