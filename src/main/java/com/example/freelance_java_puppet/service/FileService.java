package com.example.freelance_java_puppet.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileService {

    private static final String UPLOAD_DIRECTORY = "uploads"; // Root directory for uploads
    private static final String IMAGE_DIRECTORY = UPLOAD_DIRECTORY + "/image"; // Image folder
    private static final String AUDIO_DIRECTORY = UPLOAD_DIRECTORY + "/audio"; // Audio folder

    public String uploadFile(MultipartFile file, String fileType) {
        try {
            // Define the folder path based on the file type (audio or image)
            String folderPath = (fileType.equalsIgnoreCase("audio")) ? AUDIO_DIRECTORY : IMAGE_DIRECTORY;

            // Create the directory if it does not exist
            File directory = new File(folderPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // File path where it will be saved
            String filePath = folderPath + "/" + file.getOriginalFilename();

            // Save the file to the local file system
            File dest = new File(filePath);
            file.transferTo(dest);

            // Return the relative URL to the file (which will be accessible via /uploads)
            return "/uploads/" + file.getOriginalFilename();  // We store the relative URL
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file");
        }
    }
}

