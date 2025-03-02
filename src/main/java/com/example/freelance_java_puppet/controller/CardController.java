package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.DTO.CardDTO;
import com.example.freelance_java_puppet.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/history-by-userCard/{userId}")
    public CardDTO getCard(@PathVariable("userId") int userId) {
        return cardService.getCardWithHistories(userId);
    }
}
