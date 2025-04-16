package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.configuration.MqttPublisher;
import com.example.freelance_java_puppet.entity.Category;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import com.example.freelance_java_puppet.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private FileService fileService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MqttPublisher mqttPublisher;

    public History addHistory(String name, String description, MultipartFile imageFile, MultipartFile audioFile, double price) {
        // Step 1: Upload files and get their relative URLs
        String imageFileUrl = fileService.uploadFile(imageFile, "image");
        String audioFileUrl = fileService.uploadFile(audioFile, "audio");

        // Step 2: Create History entity and set the attributes
        History history = new History();
        history.setName(name);
        history.setDescription(description);
        history.setImage(imageFileUrl);  // Store the relative URL for the image
        history.setAudio(audioFileUrl);  // Store the relative URL for the audio
        history.setPrice(price);

        // Step 3: Save the history object to the database
        return historyRepository.save(history);
    }


    //get all history
    public List<HistoryDTO> getAllHistory(){
        List<HistoryDTO> historyDTOs = historyRepository.findAll().stream()
                .map(history -> {
                    HistoryDTO historyDTO = new HistoryDTO();
                    historyDTO.setName(history.getName());
                    // historyDTO.setAudio(history.getAudio());
                    historyDTO.setImage(history.getImage());
                    // historyDTO.setDescription(history.getDescription());
                    historyDTO.setPrice(history.getPrice());
                    return historyDTO;
                })
                .collect(Collectors.toList());

        return historyDTOs;
    }

    public List<HistoryDTO> getAllNonPayHistoryByUser(int userId){

        List<History> histories = historyRepository.findHistoriesNotPurchasedByUser(userId);
        return histories.stream()
                .map(history -> {
                    HistoryDTO historyDTO = new HistoryDTO();
                    historyDTO.setId(history.getId());
                    historyDTO.setName(history.getName());
                    historyDTO.setImage(history.getImage());
                    historyDTO.setPrice(history.getPrice());
                    historyDTO.setDescription(history.getDescription());
                    return historyDTO;
                })
                .collect(Collectors.toList());
    }

    public HistoryDTO getHistoryById(int historyId){
        History history = historyRepository.findById(historyId).orElseThrow(()-> new RuntimeException("Not history found "));

// Convert the single History object to a HistoryDTO
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setId(history.getId());
        historyDTO.setName(history.getName());
        historyDTO.setImage(history.getImage());
        historyDTO.setPrice(history.getPrice());
        historyDTO.setCategoryName(history.getCategories());
        historyDTO.setDescription(history.getDescription());  // Include description if necessary
        historyDTO.setAudio(history.getAudio());  // Include audio if necessary

        return historyDTO;
    }

    String baseUrl = "http://localhost:8082";

    // Send History to the toys

    public void sendHistoryDownloadLink(int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("User not found: " + userId);
            return;
        }

        User user = userOpt.get();
        List<History> histories = user.getHistories();

        for (History history : histories) {


            //String audioFilename = new File(history.getAudio()).getName();
            //String imageFilename = new File(history.getImage()).getName();

            String audioUrl = baseUrl + history.getAudio();
            String imageUrl = baseUrl + history.getImage();

            Map<String, Object> payload = new HashMap<>();
            payload.put("name", history.getName());
            payload.put("description", history.getDescription());
            payload.put("audioUrl", audioUrl);
            payload.put("imageUrl", imageUrl);

            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonPayload = mapper.writeValueAsString(payload);
                String topic = "history/download/" + userId;

                mqttPublisher.publishMessage(topic, jsonPayload);
                System.out.println("History sent to user: " + userId + " | History: " + history.getName());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }





}