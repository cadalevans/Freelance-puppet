package com.example.freelance_java_puppet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @Lob  // ✅ Stores large text data
    @Column(columnDefinition = "TEXT")  // ✅ Tells MySQL to use TEXT type
    private String description;

    @Lob
    private String image;

    @Lob
    private String audio;

    private double price;

    @JsonIgnore // Prevents serialization of the back relationship with 'Category'
    @ManyToMany(mappedBy = "histories")
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(mappedBy = "items")
    private List<Card> cards = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "histories")
    private List<User> users = new ArrayList<>();

    public void setCard(List<Card> cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public double getPrice() {
        return price;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getAudio() {
        return audio;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
