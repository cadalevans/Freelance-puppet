package com.example.freelance_java_puppet.entity;

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

    @JsonManagedReference // Serializes 'histories' field only
    @ManyToMany
    private List<History> histories = new ArrayList<>();

}
