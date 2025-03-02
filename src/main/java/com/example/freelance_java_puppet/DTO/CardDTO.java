package com.example.freelance_java_puppet.DTO;

import java.util.List;

public class CardDTO {


    private double totalPrice;
    private List<HistoryDTO> histories;

    // Getters and setters

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<HistoryDTO> getHistories() {
        return histories;
    }

    public void setHistories(List<HistoryDTO> histories) {
        this.histories = histories;
    }
}
