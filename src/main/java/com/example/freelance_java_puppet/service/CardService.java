package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.DTO.AddCardRequest;
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



    public CardDTO addCardWithHistory(int userId, int historyId) {
        System.out.println("________Adding history to card :________ " );
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getCard() == null) {
            Card card = new Card();
            card.setUser(user);
            user.setCard(card);
        }

        // 3️⃣ Fetch history item
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History item not found"));

        Card card = user.getCard();

        card.getHistories().add(history); // Add history item
        history.getCards().add(card);

        // 5️⃣ Calculate total price using Stream
        double totalPrice = card.getHistories()
                .stream()
                .mapToDouble(History::getPrice)
                .sum();
        card.setTotalPrice(totalPrice);

        Card savedCard = cardRepository.save(card);
        historyRepository.save(history);

        user.setCard(savedCard);
        userRepository.save(user);

        return getCardWithHistories(userId);
    }

    // Delete History from cart
    public CardDTO deleteHistoryFromCard(int userId, int historyId) {

        System.out.println("_____Delete History From Cart***** " );

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
        return getCardWithHistories(userId);
    }


    // Method to get the Card as DTO
    public CardDTO getCardWithHistories(int userId) {

        System.out.println("Executed a Retrieve the Card entity from the database: " );
        // Retrieve the Card entity from the database
       User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

       Card card = user.getCard();
       if( card == null){

           throw new RuntimeException("No card associated with the user");
       }
        // Create the CardDTO and set its properties
        CardDTO cardDTO = new CardDTO();

        cardDTO.setTotalPrice(card.getTotalPrice());

        // Convert the list of History entities to HistoryDTOs
        List<HistoryDTO> historyDTOs = card.getHistories().stream()
                .map(history -> {
                    HistoryDTO historyDTO = new HistoryDTO();
                    historyDTO.setId(history.getId());
                    historyDTO.setName(history.getName());
                    historyDTO.setPrice(history.getPrice());
                    historyDTO.setImage(history.getImage());
                    historyDTO.setAudio(history.getAudio());
                    historyDTO.setDescription(history.getDescription());
                    return historyDTO;
                })
                .collect(Collectors.toList());

        // Set the histories in the DTO
        cardDTO.setHistories(historyDTOs);

        return cardDTO;
    }

    // This method allow user to add multiple histories at the same time in card
    public CardDTO addCardWithHistories(AddCardRequest request) {
        System.out.println("________Adding multiple histories to card :________ " );

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getCard() == null) {
            Card card = new Card();
            card.setUser(user);
            user.setCard(card);
        }

        Card card = user.getCard();

        for (int historyId : request.getHistoryIds()) {
            History history = historyRepository.findById(historyId)
                    .orElseThrow(() -> new RuntimeException("History item not found"));

            card.getHistories().add(history); // Add each history item
            history.getCards().add(card);
        }

        // Calculate total price
        double totalPrice = card.getHistories()
                .stream()
                .mapToDouble(History::getPrice)
                .sum();
        card.setTotalPrice(totalPrice);

        // Save changes
        Card savedCard = cardRepository.save(card);
        user.setCard(savedCard);
        userRepository.save(user);

        return getCardWithHistories(request.getUserId());
    }

}
