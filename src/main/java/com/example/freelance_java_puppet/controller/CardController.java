package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.DTO.CardDTO;
import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.entity.Card;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/history-by-userCard/{userId}")
    public List<HistoryDTO> getCard(@PathVariable("userId") int userId) {
        return cardService.getCardWithHistories(userId);
    }

    @PostMapping("/add/{userId}/{historyId}")
    public ResponseEntity<Card> addCard(@PathVariable("userId") int userId, @PathVariable("historyId") int historyId) {
        Card card = cardService.addCardWithHistory(userId, historyId);
        return ResponseEntity.ok(card);
    }


}
