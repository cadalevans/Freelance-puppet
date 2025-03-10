package com.example.freelance_java_puppet.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    // Define the base upload directory as an absolute path
    private static final Path BASE_UPLOAD_DIRECTORY = Paths.get(System.getProperty("user.dir"), "uploads");

    // Define subdirectories for images and audio
    private static final Path IMAGE_DIRECTORY = BASE_UPLOAD_DIRECTORY.resolve("image");
    private static final Path AUDIO_DIRECTORY = BASE_UPLOAD_DIRECTORY.resolve("audio");

    public String uploadFile(MultipartFile file, String fileType) {
        try {
            // Determine the target directory based on the file type
            Path targetDirectory;
            if ("audio".equalsIgnoreCase(fileType)) {
                targetDirectory = Paths.get("uploads", "audio");
            } else if ("image".equalsIgnoreCase(fileType)) {
                targetDirectory = Paths.get("uploads", "image");
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
            }

            // Create the target directory if it doesn't exist
            if (Files.notExists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }

            // Define the target file path
            Path targetFilePath = targetDirectory.resolve(file.getOriginalFilename());

            // Save the file to the target location
            file.transferTo(targetFilePath.toFile());

            // Return the relative URL to access the file
            return "/uploads/" + fileType + "/" + file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file", e);
        }
    }

}

