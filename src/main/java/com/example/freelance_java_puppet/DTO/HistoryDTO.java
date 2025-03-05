package com.example.freelance_java_puppet.DTO;

import lombok.Data;

@Data
public class HistoryDTO {

    private String name;
    private String description;
    private String image;
    private String audio;
    private double price;

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
}
