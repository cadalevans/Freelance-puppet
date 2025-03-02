package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private FileService fileService;

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
}