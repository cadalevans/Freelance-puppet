package com.example.freelance_java_puppet.DTO;

import com.example.freelance_java_puppet.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class HistoryDTO {

    private int id;
    private String name;
    private String description;
    private String image;
    private String audio;
    private double price;

    private List<Category> categoryName;

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudio() {
        return audio;
    }

    public double getPrice() {
        return price;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Category> getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(List<Category> categoryName) {
        this.categoryName = categoryName;
    }
}
