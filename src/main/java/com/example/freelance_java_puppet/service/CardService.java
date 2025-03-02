package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.DTO.CardDTO;
import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.entity.Card;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.repository.CardRepository;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import com.example.freelance_java_puppet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private HistoryRepository historyRepository;

    public Card deleteHistoryFromCard(int userId, int historyId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No user found"));

        Card card = user.getCard();

        if (card == null) {
            throw new RuntimeException("No card associated with the user");
        }

        // Fetch the History entity by its historyId
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History with id " + historyId + " doesn't exist"));

        if (!card.getHistories().contains(history)) {
            throw new RuntimeException("This History is not associated with the Card");
        }

        card.getHistories().remove(history);

        // Calculate the new total price for the Card based on the remaining Histories
        double totalPrice = card.getHistories().stream()
                .mapToDouble(History::getPrice)
                .sum(); // Sum up the prices of all remaining History items


        card.setTotalPrice(totalPrice);

        // Save the updated Card (this will persist the changes)
        cardRepository.save(card);

        // Optionally, delete the History if you want to remove it from the database
        // historyRepository.delete(history);  // Uncomment this line if you want to delete the history

        // Return the updated Card
        return card;
    }


    // Method to get the Card as DTO
    public CardDTO getCardWithHistories(int userId) {
        // Retrieve the Card entity from the database
       User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

       Card card = user.getCard();
        // Create the CardDTO and set its properties
        CardDTO cardDTO = new CardDTO();

        cardDTO.setTotalPrice(card.getTotalPrice());

        // Convert the list of History entities to HistoryDTOs
        List<HistoryDTO> historyDTOs = card.getHistories().stream()
                .map(history -> {
                    HistoryDTO historyDTO = new HistoryDTO();
                    historyDTO.setName(history.getName());
                    historyDTO.setPrice(history.getPrice());
                    return historyDTO;
                })
                .collect(Collectors.toList());

        // Set the histories in the DTO
        cardDTO.setHistories(historyDTOs);

        return cardDTO;
    }


}
