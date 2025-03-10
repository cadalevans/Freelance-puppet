package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@CrossOrigin("**")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @PostMapping("/add")
    public ResponseEntity<History> addHistory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam MultipartFile imageFile,
            @RequestParam MultipartFile audioFile,
            @RequestParam double price) {

        // Call service to add history and save files
        History history = historyService.addHistory(name, description, imageFile, audioFile, price);

        // Return response
        return ResponseEntity.status(HttpStatus.CREATED).body(history);
    }


    @GetMapping("/get-all-non-pay-history/{userId}")
    public List<HistoryDTO> getAllNonPayHistory(@PathVariable("userId")int userId){
        return historyService.getAllNonPayHistoryByUser(userId);
    }

    @GetMapping("/history-by-id/{historyId}")
    public HistoryDTO getHistoryById(@PathVariable("historyId")int historyId){

        return historyService.getHistoryById(historyId);
    }
}
