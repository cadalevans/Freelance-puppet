package com.example.freelance_java_puppet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String name;

    @JsonIgnore // Serializes 'histories' field only
    @ManyToMany
    private List<History> histories = new ArrayList<>();

    public List<History> getHistories() {
        return histories;
    }

    public String getName() {
        return name;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public void setName(String name) {
        this.name = name;
    }
}
