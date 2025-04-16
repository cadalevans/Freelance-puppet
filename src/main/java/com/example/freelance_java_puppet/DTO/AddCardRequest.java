package com.example.freelance_java_puppet.DTO;

import java.util.List;

public class AddCardRequest {

    private int userId;
    private List<Integer> historyIds;

    public List<Integer> getHistoryIds() {
        return historyIds;
    }

    public int getUserId() {
        return userId;
    }

    public void setHistoryIds(List<Integer> historyIds) {
        this.historyIds = historyIds;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
